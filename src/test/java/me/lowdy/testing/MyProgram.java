package me.lowdy.testing;

public class MyProgram {

    public static void main(String[] args) {
        int i = 0;
        int j = 10;
        int[] arr = new int[5];
        String str = "Hello, World!";
        boolean b = true;

        label:
        for (i = 0; i < j; i++) {
            if (i == 3) {
                continue;
            }
            if (i == 7) {
                break;
            }
            switch(i) {
                case 0:
                    arr[i] = 0;
                    break;
                case 1:
                    arr[i] = 1;
                    break;
                case 2:
                    arr[i] = 2;
                    break label;
                default:
                    arr[i] = -1;
            }
        }

        int k = 0;
        while (k < arr.length) {
            System.out.println(arr[k]);
            k++;
        }
        System.out.println(simpleAdd());

        do {
            System.out.println("Hello, World!");
            double random = Math.random();
            if (random < 0.5) {
                break;
            }
        } while (b);

        int finalI = i;
        Runnable run = () -> {
            System.out.println("From Run " + finalI);
        };
        run.run();

        if (i > j) {
            System.out.println("i > j");
        } else {
            System.out.println("i <= j");
        }

        for (int x : arr) {
            System.out.println(x);
        }

        str += "!";
        System.out.println(str);

        int result = add(3, 4);
        System.out.println(result);
    }

    public static int add(int a, int b) {
        return a + b;
    }

    public static int simpleAdd() {
        int a = 100;
        int b = 200;
        return add(a, b);
    }
}