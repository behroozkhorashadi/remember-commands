#! /usr/bin/env python
""" An executable python script that handles the remember command of the store.

This module runs the remember portion of the command store interaction. It
allows you to query all the stored commands and also delete them if you choose.
"""
from remember import command_store_lib
import handle_args
from interactive import InteractiveCommandExecutor


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
    return run_remember_command(args.save_dir, args.json, args.history_file_path, args.query, args.all,
                                args.startswith, args.execute)


def run_remember_command(save_dir, use_json, history_file_path, query, search_all, search_starts_with, execute):
    store_file_path = command_store_lib.get_file_path(save_dir, use_json)
    store = command_store_lib.load_command_store(store_file_path, use_json)
    print('Looking for all past commands with: ' + ", ".join(query))
    result = store.search_commands(query, search_starts_with, search_info=search_all)
    print("Number of results found: " + str(len(result)))
    if execute:
        command_executor = InteractiveCommandExecutor(history_file_path)
        if not command_executor.run(result):
            return 'Exit'
    return command_store_lib.print_commands(result, query)


if __name__ == "__main__":
    print(main())
