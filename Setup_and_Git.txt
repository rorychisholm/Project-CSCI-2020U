# Installation
sudo apt-get install git
sudo apt-get install default-jdk
sudo apt-get install gradle
sudo apt-get install gedit
sudo apt-add-repository ppa:webupd8team/java
sudo apt-get update
sudo apt-get install oracle-java8-installer
- download tar.gz of IntelliJ IDEA
- move it to proper folder
tar xfz ideaIC-15.0.1.tar.gz

# Git
## Prep the directory
mkdir 01_git
cd 01_git

## Global Git config
git config --global user.email "randy.fortier@uoit.ca"
git config --global user.name "randyfortier"

## Create a repository
git init
git add .
git commit -m "Initial commit"

## Add a .gitignore file
gedit .gitignore
  *.class
  *.java~
git add .gitignore

## Add a source file
mkdir src
gedit src/HelloWorld.java
git add src
git commit -m "Added first source file"

## Create a repository on GitHub
git remote add origin https://github.com/randyfortier/test_git2.git

## Push local repository to GitHub repository
git push -u origin master              (origin - remote name, master - branch name)
(8 char)

## Compile, run, and test out .gitignore
cd src
javac *.java
java HelloWorld
cd ..
git status

# Multiple users (simulated)

## Create a clone of the repository
cd ..
mkdir 01_git_otheruser
cd 01_git_otheruser
git clone https://github.com/randyfortier/test_git2.git
cd test_git2

## Make changes to the clone
gedit src/HelloWorld.java
git commit -am "Modified the greeting"   (a - automatically include changes to already added files)
git push -u origin master

## Pull the changes from the original
git fetch    (pull changes)
git status   (compare changes)
git pull

# Branching

## Create a new branch
git branch experimental
git checkout experimental

## Make changes to the new branch
gedit src/HelloWorld.java
git commit -cm "Added version number"
git push -u origin experimental
- show on GitHub

## Merge the branches
git checkout master
git merge experimental
git push -u origin master
