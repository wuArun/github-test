package com.arun.jenkins.jenkins.test;

interface PersonFactory<P extends Person> {
    P create(String firstName, String lastName);
}
