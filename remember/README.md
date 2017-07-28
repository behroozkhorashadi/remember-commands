# remember-commands
This simple python library scrapes your bash history file and generates a searchable cummulative history that is persisted, easily searchable, and runnable.

## Setup
You're going to need to add a few things to your bash profile. These following commands make sure that all your terminals update your history file and increases the size of your history file.

Add the following to your bash profile:
```
shopt -s histappend                      # append to bash_history if Terminal.app quits
export HISTCONTROL=ignoredups:erasedups  # no duplicate entries
export HISTSIZE=32768                    # big big history
export HISTFILESIZE=32768                # big big history
export AUTOFEATURE=true autotest
# After each command, append to the history file and reread it
export PROMPT_COMMAND="${PROMPT_COMMAND:+$PROMPT_COMMAND$'\n'}history -a; history -c; history -r"
export HISTIGNORE="ls:cd:cd:remember -:pwd:exit:date:* --help";
```

## Saving history
Remember works by scraping your history file and organizing the commands in that file. This is done by running *generate_store.py*.

example:
```
generate_store.py [path_to_history_file] [path_to_a_save_directory]
```

The second argument is where you want all the meta data from the command organization to be stored. This is going to include a pickle file and some other data used by the remember command.

This command should be run periodically and will scrap only the commands that haven't been seen yet. I have set it up as a cron job that just runs on my machine every couple days.

## Ignore rules
You can add a ignore rule file to the save directory that allows the generate command to ignore certain commands. The rule file works as follows:

[s|m|c]\: text to ignore

- 's' signifies starts with
- 'm' signifies exactly matches
- 'c' signifies contains

An example is below:
```
s: git commit -a -m
s: ./remember.py
m: git log
m: arc diff
s: git commit -a --amend
s: git commit --amend
s: cd
```

## Using Remember
./remember.py [path_to_the_save_directory] [path_to_history_file] [options] [text to search]

I've aliased ./remember.py for convenience as follows:
```
alias re='~/my_git_repo/remember-commands/remember.py ~/RememberStoreDirectory/ ~/.bash_history'
```
and then use it as follows
```
re git log
```