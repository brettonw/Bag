#! /usr/bin/env perl

use strict;
use warnings FATAL => 'all';

# this is a maven wrapper intended to solve the problem that release builds don't actually deploy
# to the local nexus server using the maven release plugin

# global variables
our $mavenCommand = "mvn --quiet";
our $goPropertiesFileName = "go.properties";
our ($releaseBuildType, $snapshotBuildType) = ("", "-SNAPSHOT");

# function to do what 'chomp' should (but doesn't)
sub trim { my $s = shift; $s =~ s/^\s+|\s+$//g; return $s; };

sub setMavenVersion {
    my $newVersion = shift;
    my $newBuildType = shift;
    my $newVersionCommand = "$mavenCommand versions:set -DnewVersion=$newVersion$newBuildType -DgenerateBackupPoms=false -DprocessDependencies=false --non-recursive";
    #print "Exec ($versionCommand)\n";
    print "Setting build version ($newVersion$newBuildType)\n";
    system ($newVersionCommand) && die "Couldn't set version.\n";
}

sub checkin {
    my $message = shift;
    print "Check-in ($message).\n";
    system ("git add --all . && git commit -m 'go git ($message)' && git push origin HEAD;");
}

sub execute {
    my ($task, $command) = @_;
    print "Execute task ($task)\n";
    system ($command) && die ("($task) FAILED\n");
}

# get the version from maven
my $mvnVersionCommand = "$mavenCommand -Dexec.executable='echo' -Dexec.args='\${project.version}' --non-recursive exec:exec";
my @mvnVersionCommandOutput = `$mvnVersionCommand`;
my $version = trim ($mvnVersionCommandOutput[0]);
$version =~ s/-SNAPSHOT$//;
print "Build at version ($version)\n";

# allowed options are: [clean]? [notest]? [git]? [build* | package | install | deploy | release]
# * build is the default command
my $shouldClean = 0;
my $shouldTest = 1;
my $shouldCheckin = 0;
my $task = "build";
my %tasks; $tasks{$_} = $_ for ("build", "package", "install", "deploy", "release");
foreach (@ARGV) {
    my $arg = lc ($_);
    if ($arg eq "clean")  { $shouldClean = 1; }
    elsif ($arg eq "notest") { $shouldTest = 0; }
    elsif ($arg eq "git") { $shouldCheckin = 1; }
    elsif (exists $tasks{$arg}) { $task = $arg; }
    else { die "Unknown task ($arg).\n"; }
}

# figure out how to fulfill the task
if ($task eq "release") {
    # will be 0 if there are no changes...
    system ("git diff --quiet HEAD;") && die ("Please commit all changes before performing a release.\n");

    # proceed with the release process

    # ask the user to supply the new release version (default to the current version sans "SNAPSHOT"
    print "What is the release version (default [$version]): ";
    my $input = <STDIN>; $input = trim ($input);
    if (length ($input) > 0) { $version = $input; }

    # set the version, and execute the deploymen build
    setMavenVersion($version, $releaseBuildType);
    execute ($task, "$mavenCommand clean deploy");
    checkin("$version");
    print "Tag release ($version).\n";
    system ("git tag -a 'Release-$version' -m 'Release-$version';");

    # ask the user to supply the next development version (default to a dot-release)
    my ($major, $minor, $dot) = split (/\./, $version);
    my $nextDevelopmentVersion = "$major.$minor." . ($dot + 1);
    print "What is the new development version (default [$nextDevelopmentVersion]): ";
    $input = <STDIN>; $input = trim ($input);
    $version = (length ($input) > 0) ? $input : $nextDevelopmentVersion;
    setMavenVersion($version, $snapshotBuildType);
    checkin("$version");
} else {
    my $command = ($shouldClean == 1) ? "$mavenCommand clean" : "$mavenCommand";
    if ($task eq "build") {
        $command = ($shouldTest == 0) ? "$command compile" : "$command test";
    } else {
        $command = "$command $task";
        if ($shouldTest == 0) { $command = "$command -Dmaven.test.skip=true"; }
    }
    execute ($task, $command);
    if ($shouldCheckin) { checkin("CHECKPOINT - $version$snapshotBuildType"); }
}
