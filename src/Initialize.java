public class Initialize {
    static void CreatePageTable(){
        for (int i = 0; i <256 ; i++) {
            StoringTools.PageTable[i] = new PageTableEntries();
        }
    }
    static void CreatePhysicalMemory(){
        for (int i = 0; i <256 ; i++) {
            StoringTools.PhysicalMemory[i] = new PageFrame();
        }
    }
    static void CreateTLB(){
        for (int i = 0; i <16 ; i++) {
            StoringTools.tlb.entries[i] = new TLB_Entries();
        }
    }
}
