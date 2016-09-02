#! /usr/bin/env python

import command_store_lib
import sys
import argparse



def main():
	# args = sys.argv
	parser = argparse.ArgumentParser()
	parser.add_argument("file", help="The directory path. ex: ~/dir/where/picklefile/is")
	parser.add_argument("query", help="The term to search for. ex: 'git pull' or git")
	parser.add_argument("-d", "--delete", help="Delete commands", action="store_true")
	args = parser.parse_args()
	if not args.file:
		print "To many or too few args.\n$> remember.py [file_store_directory_path] ['word|phrase to look up']"
		return
	pickle_file_path = command_store_lib.getPickleFilePath(args.file)
	store = command_store_lib.getCommandStore(pickle_file_path)
	result = store.searchCommands(args.query)
	print "Number of results found: " + str(len(result))
	command_store_lib.CommandStore.printCommands(result)
	if args.delete and len(result) > 0:
		print "Delete mode"
		if delete_interaction(store, result):
			command_store_lib.CommandStore.pickleCommandStore(store, pickle_file_path)


def delete_interaction(store, commands):
	changesMade = False
	for command in commands:
		user_input = raw_input('Delete -->' + command.getUniqueCommandId() + '? [y|n|exit]')
		if user_input == 'y':
			print 'deleting ' + command.getUniqueCommandId()
			store.deleteCommand(command.getUniqueCommandId())
			changesMade = True
		elif user_input == 'exit':
			return changesMade
	return changesMade


if __name__ == "__main__":
	main()