import argparse
from unittest import TestCase
from remember import migrate_to_sql_db
import mock

from remember.command_store_lib import CommandStore, SqlCommandStore


class TestMain(TestCase):
    def test_setup_args_for_search_but_missing_save_dir_should_return_error_string(self):
        with mock.patch('argparse.ArgumentParser.parse_args',
                        return_value=argparse.Namespace(json=True, save_dir=None)):
            assert migrate_to_sql_db.main().startswith("To many or too few args")

    @mock.patch('remember.command_store_lib.load_command_store')
    def test_setup_args_for_search_should_make_appropriate_calls_into_command_store_lib(self,
                                                                                        store_mock):
        store_mock.side_effect = [CommandStore(), SqlCommandStore(':memory:')]
        with mock.patch('argparse.ArgumentParser.parse_args',
                        return_value=argparse.Namespace(json=True,
                                                        sql=False,
                                                        all=True,
                                                        startswith=True,
                                                        execute=False,
                                                        save_dir='save_dir',
                                                        history_file_path='hist',
                                                        query='query')):
            migrate_to_sql_db.main()
            store_mock.assert_called()
