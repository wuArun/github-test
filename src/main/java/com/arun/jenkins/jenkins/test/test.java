package com.arun.jenkins.jenkins.test;

import org.springframework.core.convert.converter.Converter;

import java.sql.SQLOutput;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class test {

    public static void main(String[] args) {
        List<String> names = Arrays.asList("peter", "anna", "mike", "xenia");
        /*Collections.sort(names, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                System.out.println(b.compareTo(a));
                return b.compareTo(a);
            }
        });*/
        /*Collections.sort(names, (a, b) -> {
            System.out.println(b.compareTo(a));
            return b.compareTo(a);
        });*/
        /*Converter<String, Integer> converter = (from) -> Integer.valueOf(from);
        Integer value = converter.convert("123");
        System.out.println(value);*/
        /*Converter<String, Integer> converter = Integer::valueOf;
        Integer value = converter.convert("123");
        System.out.println(value);*/
        /*Something something=new Something();
        Converter<String,String>  converter = something::startsWith;
        String converted = converter.convert("Java");
        System.out.println(converted); // "J"*/
        /*PersonFactory<Person> personPersonFactory = Person::new;
        Person person = personPersonFactory.create("Peter", "Parker");
        System.out.println("发晚饭");

        final int num = 1;
        Converter<Integer, Integer> stringConverter = (from) -> from + num;
        stringConverter.convert(2);*/

        /*Predicate<String> predicate = (s) -> s.length() > 0;
        predicate.test("foo"); // true
        predicate.negate().test("foo"); // false
        Predicate<Boolean> nonNull = Objects::nonNull;
        Predicate<Boolean> isNull = Objects::isNull;
        Predicate<String> isEmpty = String::isEmpty;
        Predicate<String> isNotEmpty = isEmpty.negate();

        PersonFactory<Person> personPersonFactory = Person::new;
        Person person = personPersonFactory.create("Peter", "Parker");

        Supplier<Person> personSupplier = Person::new;
        Person supplier = personSupplier.get(); // new Person*/

        /*Comparator<Person> comparator = (p1, p2) -> p1.firstName.compareTo(p2.firstName);
        Person p1 = new Person("John", "Doe");
        Person p2 = new Person("Alice", "Wonderland");
        comparator.compare(p1, p2); // > 0
        comparator.reversed().compare(p1, p2); // < 0*/

        int max = 1000000;
        List<String> values = new ArrayList<>(max);
        for (int i = 0; i < max; i++) {
            UUID uuid = UUID.randomUUID();
            values.add(uuid.toString());
        }

        long t0 = System.nanoTime();
        long count = values.stream().sorted().count();
        System.out.println(count);
        long t1 = System.nanoTime();
        long millis = TimeUnit.NANOSECONDS.toMillis(t1 - t0);
        System.out.println(String.format("sequential sort took: %d ms",millis));
    }
}
