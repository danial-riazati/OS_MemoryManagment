import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

class Translation {
    static int usedMemPageFrame = 0;
    static int pageFaults = 0;
    static int TLBHits = 0;

    public void TranslateToPhysicalAddress() {
        for (Address va : StoringTools.virtualAddresses) {
            int pageFrameNumber;
            int tlbSearch = SearchInTLB(va);
            if (tlbSearch != -1) {
                TLBHits++;
                pageFrameNumber = tlbSearch;
            } else if (isInPageTable(va)) {
                pageFrameNumber = pageFrameNumberByPageTableEntry(va);
            } else {
                pageFaults++;
                pageFrameNumber = refOnBackStoring(va);
            }
            Address physicalAddress = new Address();
            physicalAddress.PageNumber = pageFrameNumber;
            physicalAddress.Offset = va.Offset;
            StoringTools.physicalAddresses.add(physicalAddress);

        }

    }

    public boolean isInPageTable(Address va) {
        return StoringTools.PageTable[va.PageNumber].isReferenced;
    }

    public int SearchInTLB(Address va) {
        if (StoringTools.tlb.LastUsedRow == -1)
            return -1;
        for (TLB_Entries tlb_entry : StoringTools.tlb.entries)
            if (tlb_entry.VirtualPage == va.PageNumber)
                return tlb_entry.PageFrame;

        return -1;
    }

    public int refOnBackStoring(Address va) {
        try {
            byte[] b = LoadBytesFromBackStoring(va);
            int pageFrameNumber = PlaceInMemory(b);
            setPageTableEntry(pageFrameNumber, va);
            setTLBEntry(pageFrameNumber, va);
            return pageFrameNumber;

        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }

    }


    public int PlaceInMemory(byte[] b) {
        StoringTools.PhysicalMemory[usedMemPageFrame++].data = b;
        return usedMemPageFrame - 1;
    }

    void setPageTableEntry(int pageNumber, Address va) {
        StoringTools.PageTable[va.PageNumber].frameNumber = (byte) pageNumber;
        StoringTools.PageTable[va.PageNumber].isReferenced = true;
    }

    void setTLBEntry(int pageNumber, Address va) {
        if (StoringTools.tlb.LastUsedRow == 15) {
            StoringTools.tlb.LastUsedRow = 0;
        } else {
            StoringTools.tlb.LastUsedRow++;
        }
        StoringTools.tlb.entries[StoringTools.tlb.LastUsedRow].PageFrame = (byte) pageNumber;
        StoringTools.tlb.entries[StoringTools.tlb.LastUsedRow].VirtualPage = (byte) va.PageNumber;
    }

    int pageFrameNumberByPageTableEntry(Address va) {
        return StoringTools.PageTable[va.PageNumber].frameNumber;
    }

    public static byte[] LoadBytesFromBackStoring(Address va) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(Main.BackingStoreFile));
        for (int i = 0; i < 256 * va.PageNumber; i++) {
            if(scanner.hasNextInt()) {
                scanner.nextInt();
            }
            else{
                return new byte[256];
            }
        }
        byte[] b = new byte[256];
        for (int i = 0; i < 256 ; i++) {
            if(scanner.hasNextInt()) {
                b[i] = (byte)scanner.nextInt();
            }
        }
       return b;

    }
}
