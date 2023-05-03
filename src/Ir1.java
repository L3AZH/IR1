import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Ir1 {

    private final Map<String, List<Integer>> reverseIndex = new HashMap<>();
    private final TreeMap<Integer, String> queries = new TreeMap<>();
    private final String END_OF_PARAGRAPH = "/";

    public void readDocDataFromFile(String filePath, String fileName) throws IOException {
        File docFile = new File(filePath.concat(fileName));
        InputStream inputStream = new FileInputStream(docFile);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line;
        StringBuilder builder = new StringBuilder();
        int currentIndex = -1;
        while ((line = bufferedReader.readLine()) != null) {
            line = line.trim();
            if (END_OF_PARAGRAPH.equals(line)) {
                String paragraphData = builder.toString();
                List<String> words = Arrays.stream(paragraphData.split(" ")).collect(Collectors.toList());
                final int copyIndex = currentIndex;
                words.forEach(word -> {
                    String formatWord = word.trim();
                    if (formatWord.length() != 0) {
                        if (!reverseIndex.containsKey(formatWord)) {
                            List<Integer> listId = new ArrayList<>();
                            listId.add(copyIndex);
                            reverseIndex.put(formatWord, listId);
                        } else {
                            reverseIndex.get(formatWord).add(copyIndex);
                        }
                    }
                });
                currentIndex = -1;
                builder.setLength(0);
            } else {
                Integer id = isID(line);
                if (id == null) {
                    builder.append(line);
                } else {
                    currentIndex = id;
                }

            }
        }
        formatReverseIndex();
        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();
    }


    public void readQueryDataFromFile(String filePath, String fileName) throws IOException {
        File docFile = new File(filePath.concat(fileName));
        InputStream inputStream = new FileInputStream(docFile);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            line = line.trim();
            if (!END_OF_PARAGRAPH.equals(line)) {
                Integer id = isID(line);
                if (id == null) {
                    queries.put(queries.lastKey(), line);
                } else {
                    queries.put(id,"");
                }
            }
        }
        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();
    }

    public void outputTheResultFile(String filePath, String fileName) throws IOException {
        File resultFile = new File(filePath.concat(fileName));
        FileWriter fileWriter = new FileWriter(resultFile);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        for (Map.Entry<Integer, String> entry : queries.entrySet()) {
            bufferedWriter.write(String.valueOf(entry.getKey()).concat("\n"));
            List<String> wordQueries = Arrays.stream(entry.getValue().split(" "))
                    .map(String::toLowerCase)
                    .distinct()
                    .filter(word -> reverseIndex.get(word) != null)
                    .collect(Collectors.toList());
            List<Integer> result = new ArrayList<>(reverseIndex.get(wordQueries.get(0)));
            for (int index = 0; index < wordQueries.size(); index++){
                if (index != 0){
                    result = intersection(result, reverseIndex.get(wordQueries.get(index)));
                }
            }
            StringBuilder builder = new StringBuilder();
            result.forEach(id -> builder.append(id).append(" "));
            bufferedWriter.write(builder.toString().concat("\n"));
            bufferedWriter.write(END_OF_PARAGRAPH.concat("\n"));
        }
        bufferedWriter.close();
        fileWriter.close();
    }

    private List<Integer> intersection(List<Integer> list1, List<Integer> list2) {
        List<Integer> result = new ArrayList<>();
        int index1 = 0, index2 = 0;
        while (index1 < list1.size() && index2 < list2.size()) {
            if (list1.get(index1).intValue() == list2.get(index2).intValue()) {
                result.add(list1.get(index1));
                index1++;
                index2++;
            } else {
                if (list1.get(index1) < list2.get(index2)) index1++;
                else index2++;
            }
        }
        return result;
    }

    private Integer isID(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void formatReverseIndex() {
        for (Map.Entry<String, List<Integer>> entry : reverseIndex.entrySet()) {
            List<Integer> listId = entry.getValue();
            List<Integer> newFormatListId = listId.stream()
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());
            reverseIndex.put(entry.getKey(), newFormatListId);
        }
    }

    public static void main(String[] args) throws IOException {
        Ir1 ir1 = new Ir1();
        ir1.readDocDataFromFile("D:\\CODE\\CDCNPM\\IR1\\Resource\\npl\\", "doc-text");
        ir1.readQueryDataFromFile("D:\\CODE\\CDCNPM\\IR1\\Resource\\npl\\", "query-text");
        ir1.outputTheResultFile("D:\\CODE\\CDCNPM\\IR1\\src\\", "the-result.txt");
        System.out.println("Read file Success !!");
    }
}
