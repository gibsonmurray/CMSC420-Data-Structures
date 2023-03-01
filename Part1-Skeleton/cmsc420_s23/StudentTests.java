package cmsc420_s23;

public class StudentTests {
    
    public static void main(String[] args) {
        WtLeftHeap<Integer, String> heap = new WtLeftHeap<Integer, String>();
        heap.insert(94, "BWI");
        heap.insert(88, "LAX");
        heap.insert(42, "IAD");
        heap.insert(67, "DCA");
    }

}
