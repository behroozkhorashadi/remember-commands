#! /usr/bin/env python

import sys
import command_store_lib as com_lib
import os.path
import argparse

FILE_STORE_NAME = 'command_storage.txt'
IGNORE_RULE_FILE_NAME = 'ignore_rules.txt'


def main():
	parser = argparse.ArgumentParser()
	parser.add_argument("historyfile", help="The path to the history file. ex: '~/.bash_history'")
	parser.add_argument("dir", help="The directory path. ex: ~/dir/where/picklefile/is")
	args = parser.parse_args()
	pick_file_path = com_lib.getPickleFilePath(args.dir)
	commands_file_path = os.path.join(args.dir, FILE_STORE_NAME)
	ignore_rule_file = os.path.join(args.dir, IGNORE_RULE_FILE_NAME)
	print 'Using ignore rules from ' + ignore_rule_file
	store = com_lib.getCommandStore(pick_file_path)
	com_lib.readHistoryFile(store, args.historyfile, commands_file_path, ignore_rule_file)
	print 'Reading ' + args.historyfile
	com_lib.CommandStore.pickleCommandStore(store, pick_file_path)
	print 'Writing file out to ' + pick_file_path


if __name__ == "__main__":
	main()