TABLE_NAME = 'remember'

SQL_CREATE_REMEMBER_TABLE = """ CREATE TABLE IF NOT EXISTS {} (
                                        full_command TEXT PRIMARY KEY ,
                                        count_seen INTEGER NOT NULL ,
                                        last_used REAL NOT NULL , 
                                        command_info TEXT
                                    ); """.format(TABLE_NAME)
TABLE_EXISTS_QUERY = ''' SELECT count(name) FROM sqlite_master WHERE type='table' AND name='{}' '''
INSERT_INTO_REMEMBER_QUERY = ''' INSERT INTO remember(full_command,count_seen,last_used, 
    command_info) VALUES(?,?,?,?) '''
DELETE_FROM_REMEMBER = ''' DELETE FROM remember WHERE full_command=?'''
UPDATE_COUNT_QUERY = ''' UPDATE remember 
                         SET count_seen = count_seen + 1, 
                             last_used = ? 
                         WHERE rowid = ?'''
SIMPLE_SELECT_COMMAND_QUERY = "SELECT rowid FROM remember WHERE full_command = ?"
GET_ROWID_FOR_COMMAND = "SELECT rowid FROM remember WHERE full_command = ?"
SEARCH_COMMANDS_QUERY = '''SELECT * FROM remember {}'''
