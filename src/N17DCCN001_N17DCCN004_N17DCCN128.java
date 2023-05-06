import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Nhom N17DCCN001
 * NGUYEN VINH AN - N17DCCN001
 * LAM HA TUAN ANH - N17DCCN004
 * NGUYEN TIEN PHONG - N17DCCN128
 */
public class N17DCCN001_N17DCCN004_N17DCCN128 {

    /**
     * Chi muc nguoc - reverseIndex - su dung tree map de co sap xep theo alphabet cho cac tu khi them vao
     * Cau truy van - queries
     * Dau ngat cau trong file text - END_OF_PARAGRAPH
     * Danh sach loc tu - stopWords
     */
    private final Map<String, List<Integer>> reverseIndex = new TreeMap<>();
    private final TreeMap<Integer, String> queries = new TreeMap<>();
    private final String END_OF_PARAGRAPH = "/";

    private final ArrayList<String> stopWords =
            new ArrayList<>(Arrays.asList("a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are",
                    "aren't", "as", "at", "be", "because", "been", "before", "being", "below", "between", "both", "but", "by", "can't",
                    "cannot", "could", "couldn't", "did", "didn't", "do", "does", "doesn't", "doing", "don't", "down", "during",
                    "each", "few", "for", "from", "further", "had", "hadn't", "has", "hasn't", "have", "haven't", "having", "he",
                    "he'd", "he'll", "he's", "her", "here", "here's", "hers", "herself", "him", "himself", "his", "how", "how's",
                    "i", "i'd", "i'll", "i'm", "i've", "if", "in", "into", "is", "isn't", "it", "it's", "its", "itself", "let's", "me",
                    "more", "most", "mustn't", "my", "myself", "no", "nor", "not", "of", "off", "on", "once", "only", "or", "other",
                    "ought", "our", "ours	ourselves", "out", "over", "own", "same", "shan't", "she", "she'd", "she'll", "she's",
                    "should", "shouldn't", "so", "some", "such", "than", "that", "that's", "the", "their", "theirs", "them",
                    "themselves", "then", "there", "there's", "these", "they", "they'd", "they'll", "they're", "they've", "this",
                    "those", "through", "to", "too", "under", "until", "up", "very", "was", "wasn't", "we", "we'd", "we'll", "we're",
                    "we've", "were", "weren't", "what", "what's", "when", "when's", "where", "where's", "which", "while", "who",
                    "who's", "whom", "why", "why's", "with", "won't", "would", "wouldn't", "you", "you'd", "you'll", "you're",
                    "you've", "your", "yours", "yourself", "yourselves"));

    /**
     * @author Lam Ha Tuan Anh
     * 1. Doc file tu file doc-text( doc theo kieu line by line - doc tung dong)
     * 2. Loai bo nhung stop words va chuyen thanh chu thuong( lower case)
     * 2. Format lai thu tu id van ban cua tung tu
     * @param filePath - Duong dan file ( yeu cau duong dan truc tiep - neu su dung macOs/Linux su dung lenh PWD de dinh vi
     * @param fileName - ten file
     * @throws IOException
     */
    public void readDocDataFromFile(String filePath, String fileName) throws IOException {
        File docFile = new File(filePath.concat(fileName));
        InputStream inputStream = new FileInputStream(docFile);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line;
        StringBuilder builder = new StringBuilder();
        int currentIndex = -1;
        // Doc den khi ket thuc van ban
        while ((line = bufferedReader.readLine()) != null) {
            line = line.trim();
            // Dua du lieu vao Map khi doc den ki tu "/" -> ki tu ket thuc 1 van ban
            // reset id ve -1 va clear buffer cua string builder
            if (END_OF_PARAGRAPH.equals(line)) {
                String paragraphData = builder.toString().toLowerCase();
                //Tach tu va lower case va loc bo stop words
                List<String> words = Arrays.stream(paragraphData.split(" "))
                        .filter(word -> !stopWords.contains(word))
                        .collect(Collectors.toList());
                // Map tu vao chi muc nguoc -> reverse index
                final int copyIndex = currentIndex;
                words.forEach(word -> {
                    String formatWord = word.trim().toLowerCase();
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
                // Phan biet "line" la id cua van ban hay la noi dung van ban
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

    /**
     * @author Nguyen Tien Phong, Nguyen Vinh An
     * 1. Doc file query-text -> file chua cau query
     * @param filePath - Duong dan file - absolute path
     * @param fileName - ten file
     * @throws IOException
     */
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
                    queries.put(id, "");
                }
            }
        }
        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();
    }

    /**
     * @author Lam Ha Tuan Anh, Nguyen Vinh An, Nguyen Tien Phong
     * @param filePath - Duong dan tuyet doi - absolute path
     * @param fileName - ten file
     * @param isPerformWithSkipStep - output file theo 2 loai thuat toan( co buoc nhay va khong co buoc nhay)
     * @throws IOException
     */
    public void outputTheResultFile(String filePath, String fileName, boolean isPerformWithSkipStep) throws IOException {
        File resultFile = new File(filePath.concat(fileName));
        FileWriter fileWriter = new FileWriter(resultFile);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        // duyet tren danh sach query
        for (Map.Entry<Integer, String> entry : queries.entrySet()) {
            bufferedWriter.write(String.valueOf(entry.getKey()).concat("\n"));
            // Tach tung tu trong cau query -> loc bo nhung tu trung lap( distinct -> chuyen ve lower case -> loai bo stop word
            // Lay nhung tu co trong chi muc nguoc
            List<String> wordQueries = Arrays.stream(entry.getValue().split(" "))
                    .map(String::toLowerCase)
                    .distinct()
                    .filter(word -> !stopWords.contains(word))
                    .filter(word -> reverseIndex.get(word) != null)
                    .collect(Collectors.toList());
            List<Integer> result = new ArrayList<>(reverseIndex.get(wordQueries.get(0)));
            // Thuc hien phep giao voi tung tu trong cau query
            for (int index = 0; index < wordQueries.size(); index++) {
                if (index != 0) {
                    if (isPerformWithSkipStep) {
                        result = intersectionWithSkip(
                                result,
                                reverseIndex.get(wordQueries.get(index)),
                                (int) Math.sqrt(wordQueries.size()));
                    } else {
                        result = intersection(result, reverseIndex.get(wordQueries.get(index)));
                    }
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

    /**
     * @author Nguyen Vinh An - Nguyen Tien Phong
     * Phep giao theo chi muc
     * @param list1 Danh sach id cua van ban 1 ex(1 ,2 3, 5, 14 ,57, ..)
     * @param list2 Danh sach van ban 2
     * @return
     */
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

    /**
     * @author Lam Ha Tuan Anh
     * Phep giao voi buoc nhay
     * @param list1 Danh sach id cua van ban 1 ex(1 ,2 3, 5, 14 ,57, ..)
     * @param list2 danh sach van ban 2
     * @param skipStep buoc nhay - Can bac 2 cua so luong tu trong cau truy van
     * @return
     */
    private List<Integer> intersectionWithSkip(List<Integer> list1, List<Integer> list2, int skipStep) {
        List<Integer> result = new ArrayList<>();
        int index1 = 0, index2 = 0;
        while (index1 < list1.size() && index2 < list2.size()) {
            if (list1.get(index1).intValue() == list2.get(index2).intValue()) {
                result.add(list1.get(index1));
                index1++;
                index2++;
            } else {
                if (list1.get(index1) < list2.get(index2)) {
                    if (index1 + skipStep < list1.size() && list1.get(index1 + skipStep) < list2.get(index2)) {
                        while (index1 + skipStep < list1.size() && list1.get(index1 + skipStep) < list2.get(index2)) {
                            index1 += skipStep;
                        }
                    } else {
                        index1++;
                    }
                } else {
                    if (index2 + skipStep < list2.size() && list2.get(index2 + skipStep) < list1.get(index1)) {
                        while (index2 + skipStep < list2.size() && list2.get(index2 + skipStep) < list1.get(index1)) {
                            index2 += skipStep;
                        }
                    } else {
                        index2++;
                    }
                }
            }
        }
        return result;
    }

    /**
     * @author Lam Ha Tuan Anh
     * Check id khi doc file - phan biet giua so id van ban va noi dung cua van ban
     * @param input
     * @return
     */
    private Integer isID(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * @author Lam Ha Tuan Anh
     * Sap xep lai id van ban trong danh sach tu dien
     * @Example
     * "Hello" [2,1,1,3,2,5,3] -> "Hello" [1, 2, 3, 5]
     */
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

    /**
     * Chay ham nay de xuat ra file ket qua
     * Chinh lai path de khong bi loi( Win/Linux/MacOS)
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        N17DCCN001_N17DCCN004_N17DCCN128 n17DCCN001N17DCCN004N17DCCN128 = new N17DCCN001_N17DCCN004_N17DCCN128();
        //Doc file doc-text va mapping vao chi muc nguoc
        n17DCCN001N17DCCN004N17DCCN128.readDocDataFromFile("D:\\IR1\\Resource\\npl\\", "doc-text");
        //Doc file query-text
        n17DCCN001N17DCCN004N17DCCN128.readQueryDataFromFile("D:\\IR1\\Resource\\npl\\", "query-text");
        // Thuc hien phep giao
        n17DCCN001N17DCCN004N17DCCN128.outputTheResultFile("D:\\IR1\\src\\", "the-result.txt", false);
        // Thuc hien phep giao voi buoc nhay
        n17DCCN001N17DCCN004N17DCCN128.outputTheResultFile("D:\\IR1\\src\\", "the-result-with-skip-step.txt", true);
        System.out.println("Read file Success !!");
    }
}
