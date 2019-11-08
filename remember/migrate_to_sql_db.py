#! /usr/bin/env python
""" An executable python script that handles migrating from json or pickle format to sql db

usage: ./migrate.py -j /my/path/to/database/directory
"""
from __future__ import print_function
from __future__ import absolute_import

import os
from future.moves import sys

top_level_dir = os.path.dirname(os.path.dirname(os.path.realpath(__file__)))
sys.path.append(top_level_dir)

import remember.command_store_lib as command_store
from remember import handle_args


def main():
    """Entry point for this executable python module."""
    args = handle_args.setup_for_migrate()
    if not args.save_dir:
        return """To many or too few args.\n$> remember.py [
                 file_store_directory_path] [history_file_path]
                 ['word|phrase to look up']"""
    return run_migration(args.save_dir, args.json)


def run_migration(save_dir, use_json):
    store_file_path = command_store.get_file_path(save_dir, use_json, False)
    sql_store_file_path = command_store.get_file_path(save_dir, False, True)
    store = command_store.load_command_store(store_file_path, command_store.get_store_type(True,
                                                                                           False))
    sql_store = command_store.load_command_store(sql_store_file_path, command_store.SQL_STORE)
    for command in store.get_all_commands():
        sql_store.add_command(command)
    return True


if __name__ == "__main__":
    main()
