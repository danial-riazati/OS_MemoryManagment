import java.util.ArrayList;

class StoringTools {
    static ArrayList<Address> virtualAddresses = new ArrayList<>();
    static ArrayList<Address> physicalAddresses = new ArrayList<>();
    static PageTableEntries[] PageTable = new PageTableEntries[256];
    static PageFrame[] PhysicalMemory = new PageFrame[256];
    static TLB tlb = new TLB();
}
