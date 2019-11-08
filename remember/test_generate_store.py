import argparse
import os
from unittest import TestCase

import mock

from remember import generate_store
from remember import command_store_lib
from remember.command_store_lib import FILE_STORE_NAME, load_command_store, JSON_STORE, SQL_STORE

TEST_PATH_DIR = os.path.dirname(os.path.realpath(__file__))
TEST_FILES_PATH = os.path.join(TEST_PATH_DIR, "test_files")


class TestMain(TestCase):
    @mock.patch('argparse.ArgumentParser.parse_args',
                return_value=argparse.Namespace(historyfile='foo', save_dir='bar', json=True,
                                                sql=False))
    def test_when_simple_args_generate_should_be_called_with_foo_bar_and_true(self, mock_args):
        with mock.patch('remember.generate_store.generate_store_from_args') as generate_from_args:
            generate_store.main()
            generate_from_args.assert_called_once_with('foo', 'bar', True, False)

    @mock.patch('argparse.ArgumentParser.parse_args',
                return_value=argparse.Namespace(historyfile='foo', save_dir='bar', json=False,
                                                sql=False,))
    def test_when_json_arg_false_generate_should_be_called_with_foo_bar_and_false(self, mock_args):
        with mock.patch('remember.generate_store.generate_store_from_args') as generate_from_args:
            generate_store.main()
            generate_from_args.assert_called_once_with('foo', 'bar', False, False)

    def test_simple_assert_default_json_file_exists(self):
        file_path = command_store_lib.get_file_path(TEST_FILES_PATH, True)
        assert os.path.isfile(file_path)
        # store = load_command_store(file_path, JSON_STORE)
        # sql_file_path = command_store_lib.get_file_path(TEST_FILES_PATH, False, True)
        # print(sql_file_path)
        # sql_store = load_command_store(sql_file_path, SQL_STORE)
        # for item in store._command_dict.items():
        #     sql_store.add_command(item[1])
        # sql_store.close()

    def test_simple_assert_default_pickle_file_exists(self):
        file_path = command_store_lib.get_file_path(TEST_FILES_PATH, False)
        assert os.path.isfile(file_path)

    def test_when_generate_from_args_should_call_into_command_store_lib(self):
        history_file_path = 'some/path'
        commands_file_path = os.path.join(TEST_FILES_PATH, FILE_STORE_NAME)
        with mock.patch('remember.command_store_lib.read_history_file') as read_file:
            with mock.patch('remember.command_store_lib.save_command_store') as save_store:
                read_file.assert_not_called()
                generate_store.generate_store_from_args(history_file_path, TEST_FILES_PATH, True,
                                                        False)
                read_file.assert_called_once_with(mock.ANY, history_file_path, commands_file_path,
                                                  None)
                save_store.assert_called_once()

    def test_when_generate_from_args_should_use_ignore_file(self):
        tmp_holder = generate_store.IGNORE_RULE_FILE_NAME
        generate_store.IGNORE_RULE_FILE_NAME = "test_ignore_rule.txt"
        history_file_path = 'some/path'
        commands_file_path = os.path.join(TEST_FILES_PATH, FILE_STORE_NAME)
        ignore_rule_file_path = os.path.join(TEST_FILES_PATH, generate_store.IGNORE_RULE_FILE_NAME)
        with mock.patch('remember.command_store_lib.read_history_file') as read_file:
            with mock.patch('remember.command_store_lib.save_command_store') as save_store:
                read_file.assert_not_called()
                generate_store.generate_store_from_args(history_file_path, TEST_FILES_PATH, True,
                                                        False)
                read_file.assert_called_once_with(mock.ANY, history_file_path, commands_file_path, ignore_rule_file_path)
                save_store.assert_called_once()
        generate_store.IGNORE_RULE_FILE_NAME = tmp_holder
