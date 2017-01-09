#! /usr/bin/env python
#pylint: skip-file

import unittest

import command_store_lib
import os


class TestCommandStoreLib(unittest.TestCase):

    def test_CommandStore_isEmpty(self):
        command_store = command_store_lib.CommandStore()
        self.assertEqual(0, command_store.get_num_commands())

    def test_search_commands(self):
        file_name = "test_files/test_input.txt"
        store = command_store_lib.CommandStore()
        command_store_lib.read_history_file(store, file_name, "doesntmatter", None,  False)
        matches = store.search_commands(["add"])
        self.assertIsNotNone(matches)
        matches = store.search_commands(["add"], True)
        self.assertTrue(len(matches) == 0)
        matches = store.search_commands(["subl"], True)
        self.assertTrue(len(matches) == 1)

    def test_search_commands_sorted(self):
        command_store = command_store_lib.CommandStore()
        self.assertEqual(0, command_store.get_num_commands())
        command_str = "some command string"
        command = command_store_lib.Command(command_str, 10.0)
        command_store.add_command(command)
        command_str2 = "somelater command string"
        command2 = command_store_lib.Command(command_str2, 20.0)
        command_store.add_command(command2)

        result =  command_store.search_commands("some", starts_with=False, sort=True)
        self.assertEquals(result[0], command2)
        self.assertEquals(result[1], command)

    def test_addCommandToStore(self):
        command_store = command_store_lib.CommandStore()
        self.assertEqual(0, command_store.get_num_commands())
        command_str = "some command string"
        command = command_store_lib.Command(command_str)
        command_store.add_command(command)
        self.assertTrue(command_store.has_command(command))
        self.assertFalse(command_store.has_command(command_store_lib.Command("some other command")))
        self.assertEqual(1, command_store.get_num_commands())
        self.assertEqual(command,\
            command_store.get_command_by_name(command_str))
        self.assertEqual(None, command_store.get_command_by_name("non existent command string"))

    def test_getPrimaryCommand_CheckcorrectlyIdPrimaryCommand(self):
        command_str = "some command string"
        command = command_store_lib.Command(command_str)
        self.assertEqual("some", command.get_primary_command())
        command_str = "  some command string"
        command = command_store_lib.Command(command_str)
        self.assertEqual("some", command.get_primary_command())
        command_str = " . some command string"
        command = command_store_lib.Command(command_str)
        self.assertEqual("some", command.get_primary_command())

    def test_Command_checkConstructor(self):
        command = command_store_lib.Command(" git branch")
        self.assertEqual("git branch", command.get_unique_command_id())
        command = command_store_lib.Command("git branch")
        self.assertEqual("git branch", command.get_unique_command_id())

    def test_readFile(self):
        file_name = "test_files/test_input.txt"
        store = command_store_lib.CommandStore()
        command_store_lib.read_history_file(store, file_name, "doesntmatter", None,  False)
        self.assertTrue(store.has_command_by_name("vim somefile.txt"))
        self.assertTrue(store.has_command_by_name("rm somefile.txt"))
        self.assertTrue(store.has_command_by_name("whereis script"))
        self.assertTrue(store.has_command_by_name("vim /usr/bin/script"))
        self.assertFalse(store.has_command_by_name("vim somefil"))
        self.assertEqual(2, store.get_command_by_name("rm somefile.txt")
            .get_count_seen())

    def test_readFile_withIgnoreFile(self):
        file_name = "test_files/test_input.txt"
        store = command_store_lib.CommandStore()
        command_store_lib.read_history_file(store, file_name, "doesntmatter", "test_files/test_ignore_rule.txt",  False)
        self.assertFalse(store.has_command_by_name("vim somefile.txt"))
        self.assertTrue(store.has_command_by_name("rm somefile.txt"))
        self.assertTrue(store.has_command_by_name("whereis script"))
        self.assertFalse(store.has_command_by_name("vim /usr/bin/script"))
        self.assertFalse(store.has_command_by_name("vim somefil"))
        self.assertEqual(2, store.get_command_by_name("rm somefile.txt")
            .get_count_seen())

    def test_verifyPickle(self):
        file_name = "test_pickle.txt"
        command_store = command_store_lib.CommandStore()
        command_str = "git branch"
        command = command_store_lib.Command(command_str)
        command_store.add_command(command)
        command_store_lib.CommandStore.pickle_command_store(command_store, file_name)
        command_store = command_store_lib.CommandStore.load_command_store(file_name)
        self.assertTrue(command_store.has_command(command))
        os.remove(file_name)

    def test_verify_read_pickle_file(self):
        file_name = "test_files/test_pickle.txt"
        store = command_store_lib.get_command_store(file_name)
        matches = store.search_commands([""], False)
        self.assertTrue(len(matches) > 0)
        matches = store.search_commands(["rm"], True)
        self.assertTrue(len(matches) == 1)
        self.assertEqual(matches[0].get_unique_command_id(), 'rm somefile.txt')
        self.assertEqual(matches[0].get_count_seen(), 2)

    def test_verify_read_pickle_file_time(self):
        file_name = "test_files/test_pickle.txt"
        store = command_store_lib.get_command_store(file_name)
        matches = store.search_commands([""], False)
        for m in matches:
            self.assertEqual(0, m.last_used_time())

    def test_readUnproccessedLinesOnly(self):
        file_name = "test_files/test_processed.txt"
        unread_commands = command_store_lib.get_unread_commands(file_name)
        self.assertEqual("vim somefile.txt", unread_commands[0])
        self.assertEqual("git commit -a -m \"renamed directory.\"", unread_commands[1])
        self.assertEqual(2, len(unread_commands))

    def test_CurratedCommands_ReturnCorrectResults(self):
        self.assertEqual("git foo", command_store_lib.Command.get_currated_command("    git     foo"))
        self.assertEqual(". git foo", command_store_lib.Command.get_currated_command(" .   git     foo"))

    def test_delete_whenExists_shouldDeleteFromStore(self):
        file_name = "test_files/test_input.txt"
        store = command_store_lib.CommandStore()
        command_store_lib.read_history_file(store, file_name,
            "doesntmatter", None, False)
        self.assertTrue(store.has_command_by_name("vim somefile.txt"))
        self.assertIsNotNone(store.delete_command('vim somefile.txt'))
        self.assertFalse(store.has_command_by_name("vim somefile.txt"))

    def test_delete_whenDoesntExists_shouldDeleteFromStore(self):
        store = command_store_lib.CommandStore()
        self.assertIsNone(store.delete_command('anything'))

    def test_ignoreRule_whenCreate_shouldCreateWorkingIgnoreRule(self):
        file_name = "test_files/test_ignore_rule.txt"
        ignore_rule = command_store_lib.IgnoreRules.create_ignore_rule(
            file_name)
        self.assertTrue(ignore_rule.is_match('vim opensomefile'))
        self.assertFalse(ignore_rule.is_match('svim opensomefile'))
        self.assertTrue(ignore_rule.is_match('vim foo'))
        self.assertFalse(ignore_rule.is_match('svim foos'))
        self.assertTrue(ignore_rule.is_match('git commit -a -m'))
        self.assertFalse(ignore_rule.is_match('git comit -a -m'))
        self.assertFalse(ignore_rule.is_match('git foos'))

    def test_ignoreRule_whenFileDoestExist_shouldNotCrash(self):
        file_name = "test_files/test_input.txt"
        store = command_store_lib.CommandStore()
        command_store_lib.read_history_file(
            store, file_name, "doesntmatter", "test_files/fileNotthere.txt",
            False)

    def test_command_parseArgs(self):
        command_str = 'git diff HEAD^ src/b/FragmentOnlyDetector.java'
        command = command_store_lib.Command(command_str, 1234.1234)
        self.assertEqual(command.get_primary_command(), 'git')
        self.assertEqual(command.get_command_args(), ['diff', 'HEAD^', 'src/b/FragmentOnlyDetector.java'])
        self.assertEqual(1234.1234, command.last_used_time())
        command_str = 'git'
        command = command_store_lib.Command(command_str, 1234.1234)
        self.assertEqual(command.get_primary_command(), 'git')
        self.assertEqual(command.get_command_args(), [])
        self.assertEqual(1234.1234, command.last_used_time())
        command._increment_count()
        self.assertEqual(1234.1234, command.last_used_time())
        command._update_time(4321)
        self.assertEqual(4321, command.last_used_time())

if __name__ == '__main__':
    unittest.main()
