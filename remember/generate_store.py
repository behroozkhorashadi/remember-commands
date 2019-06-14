#! /usr/bin/env python
""" This module generates the store pickle file."""

import argparse
import os.path

import remember.command_store_lib as com_lib

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

    generate_store_from_args(args.historyfile, args.save_dir, args.json)


def generate_store_from_args(history_file_path, save_directory, use_json):
    store_file_path = com_lib.get_file_path(save_directory, use_json)
    commands_file_path = os.path.join(save_directory, com_lib.FILE_STORE_NAME)
    ignore_rule_file = os.path.join(save_directory, IGNORE_RULE_FILE_NAME)
    if not os.path.isfile(ignore_rule_file):
        ignore_rule_file = None
    else:
        print('Using ignore rules from ' + ignore_rule_file)
    store = com_lib.load_command_store(store_file_path, use_json)
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
