#! /usr/bin/env python
""" An executable python script that handles the remember command of the store.

This module runs the remember portion of the command store interaction. It
allows you to query all the stored commands and also delete them if you choose.
"""
from __future__ import print_function
from __future__ import absolute_import

import os
from future.moves import sys
from builtins import str

top_level_dir = os.path.dirname(os.path.dirname(os.path.realpath(__file__)))
sys.path.append(top_level_dir)

import remember.command_store_lib as command_store
from remember import handle_args
from remember.interactive import InteractiveCommandExecutor


def main():
    """Entry point for this executable python module."""
    args = handle_args.setup_args_for_search()
    if not args.save_dir:
        return """To many or too few args.\n$> remember.py [
                 file_store_directory_path] [history_file_path]
                 ['word|phrase to look up']"""
    if not args.history_file_path:
        return """To many or too few args.\n$> remember.py [
                 file_store_directory_path] [history_file_path]
                 ['word|phrase to look up']"""
    return run_remember_command(args.save_dir, args.json, args.history_file_path, args.query,
                                args.all, args.startswith, args.execute, args.sql)


def run_remember_command(save_dir, use_json, history_file_path, query, search_all,
                         search_starts_with, execute, use_sql):
    store_file_path = command_store.get_file_path(save_dir, use_json, use_sql)
    file_type = command_store.get_store_type(use_json, use_sql)
    store = command_store.load_command_store(store_file_path, file_type)
    print('Looking for all past commands with: ' + ", ".join(query))
    result = store.search_commands(query, search_starts_with, search_info=search_all)
    print("Number of results found: " + str(len(result)))
    if execute:
        command_executor = InteractiveCommandExecutor(history_file_path)
        if not command_executor.run(result):
            return 'Exit'
    return command_store.print_commands(result, query)


if __name__ == "__main__":
    main()
