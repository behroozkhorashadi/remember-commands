#! /usr/bin/env python
import sys


def main():
	args = sys.argv
	if len(args) < 3:
		print "To few args this script requires should be in the form of: [aliasName] [command]}"
		return
	command = 'alias ' + args[1] + '=' + "'" + ' '.join(args[2:]) + "'"
	print command
	mydata = raw_input('Is this what you want -->' + command + ' [y/n]')
	if (mydata == 'y'):
		alias_file_path = '/Users/beh/Google Drive/Nerd/.bash_aliases'
		print "wrote alias to file: " + alias_file_path
		with open(alias_file_path, "a") as myfile:
			myfile.write(command + '\n')
		return
	print 'Canceled write'


if __name__ == "__main__":
	main()