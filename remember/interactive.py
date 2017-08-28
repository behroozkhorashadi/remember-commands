import subprocess

from command_store_lib import bcolors

"""
This module handles the command store interactive mode.
"""


class InteractiveCommandExecutor(object):
    def __init__(self, command_store, history_file_path=None):
        self._command_store = command_store
        self._history_file_path = history_file_path

    def run(self, query_list, starts_with=False):
        result = self._command_store.search_commands(query_list,
                                                     starts_with)
        self._enumerate_commands(result)
        return self._select_command(result)

    def _enumerate_commands(self, command_results):
        for idx, command in enumerate(command_results):
            print (bcolors.HEADER + "(" + str(idx + 1) + ") " + bcolors.OKGREEN + command.get_unique_command_id()
                   + bcolors.ENDC)

    def _select_command(self, command_results):
        user_input = raw_input('Choose command by # or ' +
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


def represents_int(value):
    try:
        return int(value)
    except ValueError:
        return False
