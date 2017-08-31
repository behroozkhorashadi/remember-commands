#! /usr/bin/env python
#pylint: skip-file
import unittest

import interactive
from command_store_lib import Command
from interactive import InteractiveCommandExecutor


class Test(unittest.TestCase):

    # get_input will return 'yes' during this test
    def test_answer_yes(self):
        interactive = InteractiveCommandExecutor()
        command = Command("git rest --hard HEAD")
        command_info = 'command info'
        self.set_input('1', command_info)
        interactive.set_command_info([command])
        self.assertEqual(command.get_command_info(), command_info)
        self.reset_input()

    def set_input(self,  *args):
        self.original_raw_input = interactive.get_user_input
        interactive.get_user_input =  InputMock(args)

    def reset_input(self):
        if self.original_raw_input:
            interactive.get_user_input = self.original_raw_input

class InputMock( object ):
    def __init__(self, args):
        self.index = 0
        self.args = args
    def __call__( self, input ):
        return_value = self.args[self.index%len(self.args)]
        self.index = self.index + 1
        return return_value

if __name__ == '__main__':
    unittest.main()