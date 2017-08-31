import subprocess

import command_store_lib
from command_store_lib import bcolors

"""
This module handles the command store interactive mode.
"""


class InteractiveCommandExecutor(object):
    def __init__(self, history_file_path=None):
        self._history_file_path = history_file_path

    def run(self, result):
        """Interactively enumerate a set of commands and pick one to run."""
        self._enumerate_commands(result)
        return self._select_command(result)

    def _enumerate_commands(self, command_results):
        for idx, command in enumerate(command_results):
            print (bcolors.HEADER + "(" + str(idx + 1) + ") "
                   + bcolors.OKGREEN + command.get_unique_command_id()
                   + bcolors.ENDC)

    def _select_command(self, command_results):
        user_input = get_user_input('Choose command by # or ' +
                                    'type anything else to quit: ')
        value = represents_int(user_input)
        if value and value <= len(command_results) and value > 0:
            command = command_results[value - 1]
            if self._history_file_path:
                with open(self._history_file_path, "a") as myfile:
                    myfile.write(command.get_unique_command_id() + '\n')
            subprocess.call(command.get_unique_command_id(), shell=True)
            return True
        else:
            return False

    def set_command_info(self, command_results):
        """Interactively choose a command to set command info for."""
        self._enumerate_commands(command_results)

        user_input = get_user_input('Choose command by # or ' +
                                    'type anything else to quit: ')
        value = represents_int(user_input)
        if value and value <= len(command_results) and value > 0:
            command = command_results[value - 1]
            user_input = get_user_input('What would you like to add '
                                        'as searchable info for this command:\n')
            command.set_command_info(user_input)
            command_store_lib.print_command(1, command)
            return True
        else:
            return False

    def delete_interaction(self, store, commands):
        """Delete a command from the store."""
        changes_made = False
        ask = True
        for command in commands:
            if ask:
                user_input = get_user_input('Delete -->'
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

def get_user_input(msg):
    return raw_input(msg)

def represents_int(value):
    try:
        return int(value)
    except ValueError:
        return False
