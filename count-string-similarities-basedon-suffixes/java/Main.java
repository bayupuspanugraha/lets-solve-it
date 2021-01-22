import java.util.ArrayList;

public class Main {
    private int calculateSimilarities(String suffix, String original) {
        int counter = 0;
        for(int it=0;it<suffix.length();it++) {
            if(suffix.substring(it, it+1).equals(original.substring(it, it+1))) {
                counter++;
            } else {
                break;
            }
        }

        return counter;
    }

    private int scanString(String original) {
        int counter = 0;
        for(int idx=-1;idx<original.length() -1;idx++) {
            String suffix = original;
            if(idx>-1) {
                suffix = original.substring(idx+1);
            }

            counter += calculateSimilarities(suffix, original);
        }

        return counter;
    } 

    public ArrayList<Integer> getCountSimilarities(String[] datas) {
        long startTime = System.currentTimeMillis();

        ArrayList<Integer> results = new ArrayList<Integer>();
        for(String original: datas) {
            int counter = scanString(original);
            
            results.add(counter);
        }

        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;

        System.out.println("Execution time in milliseconds: " + timeElapsed);

        return results;
    }

    public static void main(String[] args) {
        String[] samples = new String[] {"bcaabcbca", "ddcabaddcb", "rtortortop", "cakikicaci"};
        Main m = new Main();
        System.out.println("Result: " + m.getCountSimilarities(samples));
    }
}