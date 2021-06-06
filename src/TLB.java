class TLB{
    int LastUsedRow ;
    TLB_Entries[] entries;
    TLB(){
        LastUsedRow = -1;
        entries = new TLB_Entries[16];
    }
}
class TLB_Entries {
    Byte PageFrame,VirtualPage;
    TLB_Entries(){
        PageFrame = VirtualPage = -1;
    }
}