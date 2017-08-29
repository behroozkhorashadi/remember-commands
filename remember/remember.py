#! /usr/bin/env python
""" An executable python script that handles the remember command of the store.

This module runs the remember portion of the command store interaction. It
allows you to query all the stored commands and also delete them if you choose.
"""
import argparse
import command_store_lib
import handle_args
from interactive import InteractiveCommandExecutor


def main():
    """Entry point for this executable python module."""
    args = handle_args.setup_args_for_search()
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
    print 'Looking for all past commands with: ' + ", ".join(args.query)
    result = store.search_commands(args.query, args.startswith, search_info=args.all)
    print "Number of results found: " + str(len(result))
    if args.execute:
        command_executor = InteractiveCommandExecutor(store, args.history_file_path)
        if not command_executor.run(result):
            print 'Exit'
        return
    command_store_lib.print_commands(result, args.query)


if __name__ == "__main__":
    main()
