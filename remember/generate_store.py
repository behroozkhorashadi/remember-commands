#! /usr/bin/env python
""" This module generates the store pickle file."""
from __future__ import print_function
from __future__ import absolute_import

import os.path

from future.moves import sys

top_level_dir = os.path.dirname(os.path.dirname(os.path.realpath(__file__)))
sys.path.append(top_level_dir)
from remember.handle_args import setup_args_for_generate
import remember.command_store_lib as com_lib

IGNORE_RULE_FILE_NAME = 'ignore_rules.txt'


def main():
    """Main entry point for this module."""
    args = setup_args_for_generate()
    generate_store_from_args(args.historyfile, args.save_dir, args.json, args.sql)


def generate_store_from_args(history_file_path, save_directory, use_json, use_sql):
    store_file_path = com_lib.get_file_path(save_directory, use_json, use_sql)
    commands_file_path = os.path.join(save_directory, com_lib.FILE_STORE_NAME)
    ignore_rule_file = os.path.join(save_directory, IGNORE_RULE_FILE_NAME)
    if not os.path.isfile(ignore_rule_file):
        ignore_rule_file = None
    else:
        print('Using ignore rules from ' + ignore_rule_file)
    store_type = com_lib.get_store_type(use_json, use_sql)
    store = com_lib.load_command_store(store_file_path, store_type)
    com_lib.read_history_file(
        store,
        history_file_path,
        commands_file_path,
        ignore_rule_file)
    print('Reading ' + history_file_path)
    if com_lib.save_command_store(store, store_file_path, use_json):
        print('Writing file out to ' + store_file_path)


if __name__ == "__main__":
    main()
