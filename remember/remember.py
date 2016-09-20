#! /usr/bin/env python
""" An executable python script that handles the remember command of the store.

This module runs the remember portion of the command store interaction. It
allows you to query all the stored commands and also delete them if you choose.
"""
import argparse
import command_store_lib


def main():
    """Entry point for this executable python module."""
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "-d",
        "--delete",
        help="Delete mode where you able to delete commands from the store.",
        action="store_true")
    parser.add_argument(
        "-s",
        "--startswith",
        help="Show only commands that strictly start with input command.",
        action="store_true")
    parser.add_argument(
        "file",
        help="The directory path. ex: ~/dir/where/picklefile/is")
    parser.add_argument(
        "query",
        nargs='+',
        help="The term to search for. ex: 'git pull' or git")
    args = parser.parse_args()
    if not args.file:
        print """To many or too few args.\n$> remember.py [
                 file_store_directory_path] ['word|phrase to look up']"""
        return
    pickle_file_path = command_store_lib.get_pickle_file_path(args.file)
    store = command_store_lib.get_command_store(pickle_file_path)
    print 'Looking for all past commands with: ' + ", ".join(args.query)
    result = store.search_commands(args.query, args.startswith)
    print "Number of results found: " + str(len(result))
    command_store_lib.CommandStore.print_commands(result)
    if args.delete and len(result) > 0:
        print "Delete mode"
        if _delete_interaction(store, result):
            command_store_lib.CommandStore.pickle_command_store(
                store, pickle_file_path)


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
