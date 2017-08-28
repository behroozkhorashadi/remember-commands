#! /usr/bin/env python
""" An executable python script that handles the remember command of the store.

This module runs the remember portion of the command store interaction. It
allows you to query all the stored commands and also delete them if you choose.
"""
import argparse
import command_store_lib
from interactive import InteractiveCommandExecutor


def main():
    """Entry point for this executable python module."""
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "-d",
        "--delete",
        help="Delete mode where you able to delete commands from the store.",
        action="store_true")
    parser.add_argument(
        "-j",
        "--json",
        help="Use jsonpickle to serialize/deserialize the store.",
        action="store_true")
    parser.add_argument(
        "-s",
        "--startswith",
        help="Show only commands that strictly start with input command.",
        action="store_true")
    parser.add_argument(
        "-i",
        "--interactive",
        help="Execute the searched commands.",
        action="store_true")
    parser.add_argument(
        "save_dir",
        help="The directory path. ex: ~/dir/where/serializedfile/is")
    parser.add_argument(
        "history_file_path",
        help="The path to the history file. ex: ~/.bash_history")
    parser.add_argument(
        "query",
        nargs='+',
        help="The term to search for. ex: 'git pull' or git")
    args = parser.parse_args()
    if not args.save_dir:
        print """To many or too few args.\n$> remember.py [
                 file_store_directory_path] [history_file_path]
                 ['word|phrase to look up']"""
        return
    if not args.history_file_path:
        print """To many or too few args.\n$> remember.py [
                 file_store_directory_path] [history_file_path]
                 ['word|phrase to look up']"""
        return

    store_file_path = command_store_lib.get_file_path(args.save_dir, args.json)
    store = command_store_lib.load_command_store(store_file_path, args.json)
    if args.interactive:
        command_executor = InteractiveCommandExecutor(store, args.history_file_path)
        if not command_executor.run(args.query, args.startswith):
            print 'Exit'
        return
    print 'Looking for all past commands with: ' + ", ".join(args.query)
    result = store.search_commands(args.query, args.startswith)
    print "Number of results found: " + str(len(result))
    command_store_lib.print_commands(result, args.query)
    if args.delete and len(result) > 0:
        print "Delete mode"
        if _delete_interaction(store, result):
            command_store_lib.save_command_store(store, store_file_path, args.json)


def _delete_interaction(store, commands):
    changes_made = False
    ask = True
    for command in commands:
        if ask:
            user_input = raw_input('Delete -->'
                                   + command.get_unique_command_id() +
                                   '? [y|n|exit|allofthem]')
        else:
            user_input = 'y'
        if user_input == 'y':
            print 'deleting ' + command.get_unique_command_id()
            store.delete_command(command.get_unique_command_id())
            changes_made = True
        elif user_input == 'exit':
            return changes_made
        elif user_input == 'allofthem':
            print 'deleting ' + command.get_unique_command_id()
            store.delete_command(command.get_unique_command_id())
            ask = False
    return changes_made


if __name__ == "__main__":
    main()
