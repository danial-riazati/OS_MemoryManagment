import java.io.*;
import java.util.Scanner;

public class Main {
    static String InputFile, OutputFile, BackingStoreFile;

    public static void main(String[] args) throws IOException {

        InputFile = args[0];
        OutputFile = args[1];
        BackingStoreFile = args[2];
        LoadVirtualAddressesFromFile();
        Initialize.CreatePageTable();
        Initialize.CreatePhysicalMemory();
        Initialize.CreateTLB();
        Translation translation = new Translation();
        translation.TranslateToPhysicalAddress();
        SetPhysicalAddressesToFile();
    }

    public static void LoadVirtualAddressesFromFile() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(InputFile));
        while (scanner.hasNextInt()) {
            int x = scanner.nextInt();
            StoringTools.virtualAddresses.add(ExtractVirtualAddress(x));
        }

    }

    public static int ExtractValue(Address physicalAddress) {
        if (physicalAddress.PageNumber < 0)
            return StoringTools.PhysicalMemory[256 + physicalAddress.PageNumber].data[physicalAddress.Offset];

        return StoringTools.PhysicalMemory[physicalAddress.PageNumber].data[physicalAddress.Offset];
    }

    public static void SetPhysicalAddressesToFile() throws FileNotFoundException {
        /*Convert Extracted Physical addresses to File*/
        StringBuilder data = new StringBuilder();
        for (int i = 0; i < StoringTools.physicalAddresses.size(); i++) {
            int virtualAddress = StoringTools.virtualAddresses.get(i).PageNumber * 256 + StoringTools.virtualAddresses.get(i).Offset;
            int physicalAddress = StoringTools.physicalAddresses.get(i).PageNumber * 256 + StoringTools.physicalAddresses.get(i).Offset;
            data.append(String.format("Virtual Address: %d - Physical Address: %d - Value: %d\n"
                    , virtualAddress, physicalAddress, ExtractValue(StoringTools.physicalAddresses.get(i))));
        }
        data.append(String.format("Number of Translated Addresses = %d\nPage Faults = %d\nPage Fault Rate = %.3f\nTLB Hits = %d\nTLB Hit Rate = %.3f\n"
                , StoringTools.virtualAddresses.size()
                , Translation.pageFaults, (float) Translation.pageFaults / StoringTools.physicalAddresses.size()
                , Translation.TLBHits, (float) Translation.TLBHits / StoringTools.physicalAddresses.size()));
        PrintWriter pw = new PrintWriter(Main.OutputFile);
        pw.print(data);
        pw.close();

    }

    public static Address ExtractVirtualAddress(int data) {
        Address va = new Address();
        va.Offset = (((1 << 8) - 1) & (data >> (0)));
        va.PageNumber = (((1 << 8) - 1) & (data >> (9 - 1)));
        return va;
    }


}

