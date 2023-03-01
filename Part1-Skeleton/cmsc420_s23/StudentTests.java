package cmsc420_s23;

public class StudentTests {
    
    public static void main(String[] args) {
        WtLeftHeap<Integer, String> heap1 = new WtLeftHeap<Integer, String>();
        heap1.insert(88, "BWI");
        heap1.insert(61, "LAX");
        heap1.insert(47, "IAD");
        heap1.insert(33, "DCA");
        heap1.insert(26, "JFK");
        heap1.insert(52, "ATL");
        heap1.insert(13, "PVD");

        WtLeftHeap<Integer, String> heap2 = new WtLeftHeap<Integer, String>();
        heap2.insert(93, "SEA");
        heap2.insert(82, "CLT");
        heap2.insert(72, "BOS");
        heap2.insert(37, "ORD");
        heap2.insert(55, "DFW");
        heap2.insert(23, "DEN");
        Locator loc = heap2.insert(11, "MIA");

        heap1.mergeWith(heap2);

    }

}
