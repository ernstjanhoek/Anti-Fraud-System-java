import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class GenerateCats {

    public static List<Cat> generateNCats(int n) {
        ArrayList<Cat> catList = new ArrayList<>();
        Stream.generate(Cat::new).limit(n).forEach(catList::add);
        return catList;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        List<Cat> cats = generateNCats(scanner.nextInt());

        System.out.println(cats.size());
    }
}

class Cat {
    String name;
    int age;
}