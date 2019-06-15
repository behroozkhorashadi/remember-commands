#! /usr/bin/env python
""" This module generates the store pickle file."""
from __future__ import print_function
from __future__ import absolute_import

import argparse
import os.path

import remember.command_store_lib as com_lib

FILE_STORE_NAME = 'command_storage.txt'
IGNORE_RULE_FILE_NAME = 'ignore_rules.txt'


def main():
    """Main entry point for this module."""
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "historyfile",
        help="The path to the history file. ex: '~/.bash_history'")
    parser.add_argument(
        "save_dir", help="The directory path. ex: ~/dir/where/serializedfile/is")
    parser.add_argument(
        "-j",
        "--json",
        help="Use jsonpickle to serialize/deserialize the store.",
        action="store_true")
    args = parser.parse_args()
    store_file_path = com_lib.get_file_path(args.save_dir, args.json)
    commands_file_path = os.path.join(args.save_dir, FILE_STORE_NAME)
    ignore_rule_file = os.path.join(args.save_dir, IGNORE_RULE_FILE_NAME)
    if not os.path.isfile(ignore_rule_file):
        ignore_rule_file = None
    else:
        print('Using ignore rules from ' + ignore_rule_file)
    store = com_lib.load_command_store(store_file_path, args.json)
    com_lib.read_history_file(
        store,
        args.historyfile,
        commands_file_path,
        ignore_rule_file)
    print('Reading ' + args.historyfile)
    if com_lib.save_command_store(store, store_file_path, args.json):
        print('Writing file out to ' + store_file_path)


if __name__ == "__main__":
    main()
