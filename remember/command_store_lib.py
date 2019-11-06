#! /usr/bin/env python
"""
This Module contains the core logic for the remember functions.
"""
from __future__ import print_function
from future import standard_library
standard_library.install_aliases()
from builtins import str
from builtins import object
try:
    import pickle as pickle
except ImportError:
    import pickle

import os.path
import re
import shutil
import sys
import time

PROCESSED_TO_TAG = '****** previous commands read *******'
PICKLE_FILE_NAME = 'pickle_file.pickle'
JSON_FILE_NAME = 'command_store.json'
FILE_STORE_NAME = 'command_storage.txt'


def time_cmp(item):
    return item.last_used_time()


class bcolors(object):
    HEADER = '\033[95m'
    OKBLUE = '\033[94m'
    OKGREEN = '\033[92m'
    YELLOW = '\033[33m'
    WARNING = '\033[93m'
    FAIL = '\033[91m'
    ENDC = '\033[0m'
    BOLD = '\033[1m'
    UNDERLINE = '\033[4m'


class CommandStore(object):
    """
    This is the primary class that holds and interacts with all the commands.

    Provides command set manipulation methods and search capabilities.
    """

    def __init__(self):
        self._command_dict = {}

    def add_command(self, command):
        """This method adds a command to the store."""
        if command.get_unique_command_id() not in self._command_dict:
            self._command_dict[command.get_unique_command_id()] = command
        else:
            store_command = self._command_dict[command.get_unique_command_id()]
            store_command._increment_count()
            store_command._update_time(command.last_used_time())

    def delete_command(self, command_str):
        """This method deletes a command from the store. """
        return self._command_dict.pop(command_str, None)

    def has_command(self, command):
        """This method checks to see if a command is in the store. """
        return command.get_unique_command_id() in self._command_dict

    def has_command_by_name(self, command_str):
        """This method checks to see if a command (by name) is in the store.
        """
        return command_str in self._command_dict

    def get_num_commands(self):
        """This method returns the number of commands in the store."""
        return len(self._command_dict)

    def get_command_by_name(self, command_str):
        """This method gets the command by its full name."""
        if command_str in self._command_dict:
            return self._command_dict[command_str]
        return None

    def search_commands(self,
                        search_terms,
                        starts_with=False,
                        sort=True,
                        search_info=False):
        """This method searches the command store for the command given."""
        matches = []
        for _, command in self._command_dict.items():
            if starts_with:
                if (not command.get_unique_command_id().startswith(search_terms[0])):
                    continue
            if all(search_term in command.get_unique_command_id()
                   for search_term in search_terms):
                matches.append(command)
                continue
            if (search_info and
                command.get_command_info() and
                any(search_term in command.get_command_info()
                    for search_term in search_terms)):
                matches.append(command)
        if sort:
            matches.sort(key=time_cmp, reverse=True)
        return matches


def print_commands(commands, highlighted_terms=[]):
    """Pretty print the commands."""
    x = 1
    for command in commands:
        print_command(x, command, highlighted_terms)
        x = x + 1


def print_command(index, command, highlighted_terms=[]):
    """Pretty print a single command."""
    command_str = command.get_unique_command_id()
    info_str = command.get_command_info()
    for term in highlighted_terms:
        command_str = command_str.replace(term, bcolors.OKGREEN + term + bcolors.YELLOW)
        info_str = info_str.replace(term, bcolors.OKGREEN + term + bcolors.YELLOW)
    print(bcolors.HEADER + '(' + str(index) + '): ' + bcolors.YELLOW + command_str
          + bcolors.OKBLUE + " --count:" + str(command.get_count_seen()) + bcolors.ENDC)
    if info_str:
        print(bcolors.FAIL + "Command context/info: " + info_str + bcolors.ENDC)


class IgnoreRules(object):
    """ This class holds the set of ignore rules for commands."""

    def __init__(self):
        self._start_with = []
        self._contains = []
        self._matches = set()

    def is_match(self, command_str):
        """ If the command matches any of the ignore rules returns true."""
        # ignore all empty strings.
        if not command_str:
            return True
        if command_str in self._matches:
            return True
        for val in self._start_with:
            if command_str.startswith(val):
                return True
        for val in self._contains:
            if val in command_str:
                return True
        return False

    def add_starts_with(self, command_str):
        """Add a starts with rule to ignore."""
        self._start_with.append(command_str)

    def add_contains(self, command_str):
        """Add a contains with rule to ignore."""
        self._contains.append(command_str)

    def add_matches(self, command_str):
        """Add a exact matches with rule to ignore."""
        self._matches.add(command_str)

    @staticmethod
    def create_ignore_rule(src_file):
        """Generate a IgnoreRules object from the input file."""
        ignore_rules = IgnoreRules()
        methods = {
            's': ignore_rules.add_starts_with,
            'c': ignore_rules.add_contains,
            'm': ignore_rules.add_matches,
        }
        if not os.path.isfile(src_file):
            return ignore_rules
        for line in open(src_file).readlines():
            split = line.split(":", 1)
            if len(split) == 2:
                methods[split[0]](split[1].strip())
        return ignore_rules


class Command(object):
    """This class holds the basic pieces for a command."""

    def __init__(self, command_str="", last_used=time.time()):
        self._command_str = Command.get_currated_command(command_str)
        self._context_before = set()
        self._context_after = set()
        self._manual_comments = "Place any comments here."
        self._parse_command(self._command_str)
        self._count_seen = 1
        self._last_used = last_used
        self._command_info = ""

    def _parse_command(self, command):
        """Set the primary command."""
        command_split = command.split(" ")
        if command_split[0] == ".":
            self._primary_command = command_split[1]
            self._command_args = command_split[2:]
        else:
            self._primary_command = command_split[0]
            self._command_args = command_split[1:]

    def get_command_args(self):
        """Get the input args for the command"""
        # This is for backwards compatibility with earlier picked classes that
        # didn't have this field.
        if not hasattr(self, '_command_args'):
            self._parse_command(self.get_unique_command_id())
        return self._command_args

    def get_command_info(self):
        """Get the input args for the command"""
        # This is for backwards compatibility with earlier picked classes that
        # didn't have this field.
        if not hasattr(self, '_command_info'):
            self._command_info = ""
        return self._command_info

    def get_primary_command(self):
        """Get the primary command."""
        return self._primary_command

    def get_unique_command_id(self):
        """Get the commands unique id."""
        return self._command_str

    def get_count_seen(self):
        """Get the count seen."""
        return self._count_seen

    def _increment_count(self):
        """Increment the count of the command."""
        self._count_seen += 1

    def _update_time(self, updated_time):
        """Update the time to this new time."""
        self._last_used = updated_time

    def last_used_time(self):
        """Get the last used time in seconds from epoch"""
        if not hasattr(self, '_last_used'):
            self._last_used = 0
        return self._last_used

    def set_command_info(self, info):
        self._command_info = info

    @staticmethod
    def get_currated_command(command_str):
        """Given a command string currate the string and return."""
        currated_command = re.sub(' +', ' ', command_str.strip())
        if currated_command.startswith(":"):
            p = re.compile(";")
            m = p.search(currated_command)
            if m and len(currated_command) > m.start() + 1:
                currated_command = currated_command[m.start() + 1:].strip()
        return currated_command


def get_unread_commands(src_file):
    """Read the history file and get all the unread commands."""
    unproccessed_lines = []
    tmp_hist_file = src_file + '.tmp'
    shutil.copyfile(src_file, tmp_hist_file)
    try:
        for line in reversed(open(tmp_hist_file).readlines()):
            if PROCESSED_TO_TAG in line:
                return list(reversed(unproccessed_lines))
            unproccessed_lines.append(line.strip())
    finally:
        os.remove(tmp_hist_file)
    return unproccessed_lines


def read_history_file(
    store,
    src_file,
    store_file,
    ignore_file=None,
    mark_read=True):
    """Read in the history files."""

    commands = get_unread_commands(src_file)
    output = []
    if ignore_file:
        ignore_rules = IgnoreRules.create_ignore_rule(ignore_file)
    else:
        ignore_rules = IgnoreRules()
    # get the max count
    current_time = time.time()
    for line in commands:
        current_time += 1
        command = Command(line, current_time)
        if ignore_rules.is_match(command.get_unique_command_id()):
            continue
        store.add_command(command)
        output.append(command.get_unique_command_id())
    if mark_read:
        with open(store_file, 'a') as command_filestore:
            for command_str in output:
                command_filestore.write(command_str + '\n')
        with open(src_file, "a") as myfile:
            myfile.write(PROCESSED_TO_TAG + "\n")


def get_file_path(directory_path, use_json=False):
    """Get the pickle file given the directory where the files is."""
    if not use_json:
        return os.path.join(directory_path, PICKLE_FILE_NAME)
    return os.path.join(directory_path, JSON_FILE_NAME)


def _load_command_store_from_pickle(file_name):
    """Load the command store from a pickle file."""
    return pickle.load(open(file_name, "rb"))


def _load_command_store_from_json(file_name):
    """Load the command store from a pickle file."""
    try:
        import jsonpickle
    except ImportError:
        print(bcolors.FAIL + 'Trying to use jsonpickle but importing the module failed. Is it installed?'
              + bcolors.ENDC)
        return CommandStore()
    with open(file_name, "rb") as in_file:
        command_store = jsonpickle.decode(in_file.read())
        return command_store


def _pickle_command_store(command_store, file_name):
    """Pickle the whole store."""
    pickle.dump(command_store, open(file_name, "wb"))
    return True


def _jsonify_command_store(command_store, file_name):
    """Jsonify the whole store."""
    try:
        import jsonpickle
    except ImportError:
        print(bcolors.FAIL + 'Trying to use jsonpickle but importing the module failed. Is it installed?'
              + bcolors.ENDC)

    tmp_file = file_name + '.tmp'
    try:
        with open(tmp_file, "wb") as out_file:
            out_file.write(jsonpickle.encode(command_store).encode("utf-8"))
    except IOError:
        print(("Unexpected error:", sys.exc_info()[0]))
        os.remove(tmp_file)
        print("removing {} something went wrong".format(tmp_file))
        return False
    shutil.copyfile(tmp_file, file_name)
    os.remove(tmp_file)
    print("removing tmp file {} ".format(tmp_file))
    return True


def load_command_store(file_name, format_is_json=False):
    """Get the command store from the input file."""
    if format_is_json:
        load_store_method = _load_command_store_from_json
    else:
        load_store_method = _load_command_store_from_pickle
    if os.path.isfile(file_name):
        print(bcolors.OKBLUE + 'Unpacking json file ' + file_name + bcolors.ENDC)
        store = load_store_method(file_name)
    else:
        store = CommandStore()
        print(bcolors.FAIL + 'File not found: ' + file_name + bcolors.ENDC)
    return store


def save_command_store(store, filename, write_to_json=False):
    if write_to_json:
        save_store_method = _jsonify_command_store
    else:
        save_store_method = _pickle_command_store
    return save_store_method(store, filename)
