package nl.rijksoverheid.dcbs.verifier.models.data

enum class DCCTestManufacturer(val value: String) {

    QingdaoHightop("1341"),
    BectonDickinson("1065"),
    CTKBiotech("1581"),
    BioRadLaboratories("2031"),
    MEDsanGmbH("1180"),
    GAGenericAssays("1855"),
    GuangdongLongsee("1216"),
    MerlinBiomedical("2029"),
    HangzhouLaihe("1215"),
    AconBiotech("1457"),
    XiamenWiz1("1456"),
    HangzhouClongene1("1610"),
    Joinstar("1333"),
    Fujirebio("2147"),
    ShenzhenZhenrui("1574"),
    ShenzhenMicroprofit1("1178"),
    BeijingLepu("1331"),
    Eurobio("1739"),
    Artron("1618"),
    AnhuiDeepBlue1("1736"),
    SiemensHealthineers("1218"),
    MoLab("1190"),
    Goldsite("1197"),
    HangzhouImmuno("2317"),
    NewGene("1501"),
    ACONLaboratories("1468"),
    DdsDiagnostic("1225"),
    TodaPharma("1466"),
    TriplexInternationalBiosciences("1465"),
    ShenzhenMicroprofit("1223"),
    ZhezhiangOrientGene("1343"),
    BioMaxima("2035"),
    Azure("1906"),
    ArcDia1("768"),
    GuangdongHecin("1747"),
    Boditech("1989"),
    RapidPathogenScreening("2290"),
    CoreTechnology("1919"),
    HangzhouClongene2("1363"),
    Bionote("1242"),
    BeijingWantai1("1484"),
    MPBiomedicals("1481"),
    GuangdongWesail("1360"),
    SDBiosensor1("2052"),
    AssureTech1("770"),
    SGAMedikal1("1357"),
    BTNX("1236"),
    Biomerica("1599"),
    Sugentech("1114"),
    Oncosem("1199"),
    NanoRepro("2200"),
    BeijingHotgen("1870"),
    Abbott("1232"),
    HubeiJinjian("1759"),
    Prognosis("1495"),
    GenSure("1253"),
    Bioteke("2067"),
    Biosynex2("1494"),
    Quidel("1097"),
    Safecare1("1490"),
    Getein1("2183"),
    Healgen("1767"),
    AVALUN("1800"),
    Jiangsu("1920"),
    Safecare2("1489"),
    JOYSBIO("1764"),
    XiamenWiz2("1884"),
    XiamenAmonMed("1763"),
    Novatech("1762"),
    HangzhouClongene3("1365"),
    GenBody("1244"),
    EdinburghGenetics("1243"),
    BeijingWantai2("1485"),
    PCL1("308"),
    ShenzhenWatmind1("1769"),
    ShenzhenWatmind2("1768"),
    ArcDia2("2078"),
    Humasis("1263"),
    AssureTech2("2350"),
    Triplex("2074"),
    BeijingJinwofu("2072"),
    AESKU("2108"),
    Roche1("2228"),
    JiangsuBioperfectus("2107"),
    MEXACARE("1775"),
    Asan("1654"),
    NalVonMinden1("2104"),
    HangzhouAllTest("1257"),
    WuhanLife("1773"),
    VivaChek("2103"),
    DIALAB("1375"),
    AXIOM("2101"),
    AnhuiDeepBlue2("1815"),
    Tody("1934"),
    ShenzhenLvshiyuan("2109"),
    PCL2("2243"),
    DNADiagnostic("2242"),
    NesaporEuropa("2241"),
    PrecisionBiosensor("1271"),
    HangzhouTestsea("1392"),
    AnbioXiamen("1822"),
    AMEDALabordiagnostik("1304"),
    Getein2("1820"),
    PerGrande("2116"),
    LumiraDX("1268"),
    LumiQuick("1267"),
    NanoEntek("1420"),
    Labnovation("1266"),
    GreenCross("1144"),
    ArcDia3("2079"),
    WuhanUNscience("2090"),
    Genrui("2012"),
    BIOHIT("1286"),
    WuhanEasyDiagnosis("2098"),
    AtlasLink("2010"),
    NalVonMinden2("1162"),
    Affimedix("2130"),
    GuangzhouWondfo("1437"),
    AAZ_LMB("1833"),
    Lumigenex("2128"),
    JiangsuMedomics("2006"),
    BioGnost("2247"),
    XiamenBoson("1278"),
    ZhuhaiLituo("1957"),
    SGAMedikal2("1319"),
    ZhejiangAndLucky("1296"),
    ZhejiangReOpenTest("1295"),
    CerTest("1173"),
    Hangzhou("1844"),
    HangzhouLysun("2139"),
    ShenzhenUltraDiagnostics("2017"),
    SDBiosensor2("344"),
    GuangzhouDecheng("1324"),
    SDBiosensor3("345"),
    Vitrosens("1443"),
    ScheBo("1201"),
    BioticalHealth("2013"),
    RapiGEN("1606"),
    ShenzhenMicroprofit2("1967"),
    Roche2("1604");

    fun getDisplayName(): String {
        return when (this) {
            QingdaoHightop -> "Qingdao Hightop Biotech Co., Ltd, SARS-CoV-2 Antigen Rapid Test (Immunochromatography)"
            BectonDickinson -> "Becton Dickinson, BD Veritor\u2122 System for Rapid Detection of SARS CoV 2"
            CTKBiotech -> "CTK Biotech, Inc, OnSite COVID-19 Ag Rapid Test"
            BioRadLaboratories -> "Bio-Rad Laboratories / Zhejiang Orient Gene Biotech, Coronavirus Ag Rapid Test Cassette (Swab)"
            MEDsanGmbH -> "MEDsan GmbH, MEDsan SARS-CoV-2 Antigen Rapid Test"
            GAGenericAssays -> "GA Generic Assays GmbH, GA CoV-2 Antigen Rapid Test"
            GuangdongLongsee -> "Guangdong Longsee Biomedical Co., Ltd, COVID-19 Ag Rapid Test Kit (Immuno-Chromatography)"
            MerlinBiomedical -> "Merlin Biomedical (Xiamen) Co., Ltd., SARS-CoV-2 Antigen Rapid Test Cassette"
            HangzhouLaihe -> "Hangzhou Laihe Biotech Co., Ltd, LYHER Novel Coronavirus (COVID-19) Antigen Test Kit(Colloidal Gold)"
            AconBiotech -> "Acon Biotech (Hangzhou) Co., Ltd, SARS-CoV-2 Antigen Rapid Test"
            XiamenWiz1 -> "Xiamen Wiz Biotech Co., Ltd, SARS-CoV-2 Antigen Rapid Test"
            HangzhouClongene1 -> "Hangzhou Clongene Biotech Co., Ltd, COVID-19 Antigen Rapid Test Cassette"
            Joinstar -> "Joinstar Biomedical Technology Co., Ltd, COVID-19 Rapid Antigen Test (Colloidal Gold)"
            Fujirebio -> "Fujirebio, ESPLINE SARS-CoV-2"
            ShenzhenZhenrui -> "Shenzhen Zhenrui Biotechnology Co., Ltd, Zhenrui \u00aeCOVID-19 Antigen Test Cassette"
            ShenzhenMicroprofit1 -> "Shenzhen Microprofit Biotech Co., Ltd, SARS-CoV-2 Spike Protein Test Kit (Colloidal Gold Chromatographic Immunoassay)"
            BeijingLepu -> "Beijing Lepu Medical Technology Co., Ltd, SARS-CoV-2 Antigen Rapid Test Kit"
            Eurobio -> "Eurobio Scientific, EBS SARS-CoV-2 Ag Rapid Test"
            Artron -> "Artron Laboratories Inc, Artron COVID-19 Antigen Test"
            AnhuiDeepBlue1 -> "Anhui Deep Blue Medical Technology Co., Ltd, COVID-19 (SARS-CoV-2) Antigen Test Kit(Colloidal Gold)"
            SiemensHealthineers -> "Siemens Healthineers, CLINITEST Rapid Covid-19 Antigen Test"
            MoLab -> "m\u00f6Lab, m\u00f6-screen Corona Antigen Test"
            Goldsite -> "Goldsite Diagnostics Inc, SARS-CoV-2 Antigen Kit (Colloidal Gold)"
            HangzhouImmuno -> "Hangzhou Immuno Biotech Co.,Ltd, SARS-CoV-2 Antigen Rapid Test"
            NewGene -> "New Gene (Hangzhou) Bioengineering Co., Ltd, COVID-19 Antigen Detection Kit"
            ACONLaboratories -> "ACON Laboratories, Inc, Flowflex SARS-CoV-2 Antigen rapid test"
            DdsDiagnostic -> "DDS DIAGNOSTIC, Test Rapid Covid-19 Antigen (tampon nazofaringian)"
            TodaPharma -> "TODA PHARMA, TODA CORONADIAG Ag"
            TriplexInternationalBiosciences -> "Triplex International Biosciences Co., Ltd, SARS-CoV-2 Antigen Rapid Test Kit"
            ShenzhenMicroprofit -> "BIOSYNEX S.A., BIOSYNEX COVID-19 Ag BSS"
            ZhezhiangOrientGene -> "Zhezhiang Orient Gene Biotech Co., Ltd, Coronavirus Ag Rapid Test Cassette (Swab)"
            BioMaxima -> "BioMaxima SA, SARS-CoV-2 Ag Rapid Test"
            Azure -> "Azure Biotech Inc, COVID-19 Antigen Rapid Test Device"
            ArcDia1 -> "ArcDia International Ltd, mariPOC SARS-CoV-2"
            GuangdongHecin -> "Guangdong Hecin Scientific, Inc., 2019-nCoV Antigen Test Kit (colloidal gold method)"
            Boditech -> "Boditech Med Inc, AFIAS COVID-19 Ag"
            RapidPathogenScreening -> "Rapid Pathogen Screening, Inc., LIAISON\u00ae Quick Detect Covid Ag Assay"
            CoreTechnology -> "Core Technology Co., Ltd, Coretests COVID-19 Ag Test"
            HangzhouClongene2 -> "Hangzhou Clongene Biotech Co., Ltd, Covid-19 Antigen Rapid Test Kit"
            Bionote -> "Bionote, Inc, NowCheck COVID-19 Ag Test"
            BeijingWantai1 -> "Beijing Wantai Biological Pharmacy Enterprise Co., Ltd, Wantai SARS-CoV-2 Ag Rapid Test (FIA)"
            MPBiomedicals -> "MP Biomedicals, Rapid SARS-CoV-2 Antigen Test Card"
            GuangdongWesail -> "Guangdong Wesail Biotech Co., Ltd, COVID-19 Ag Test Kit"
            SDBiosensor1 -> "SD BIOSENSOR Inc, STANDARD Q COVID-19 Ag Test Nasal"
            AssureTech1 -> "Assure Tech. (Hangzhou) Co., Ltd, ECOTEST COVID-19 Antigen Rapid Test Device"
            SGAMedikal1 -> "SGA Medikal, V-Chek SARS-CoV-2 Rapid Ag Test (colloidal gold)"
            BTNX -> "BTNX Inc, Rapid Response COVID-19 Antigen Rapid Test"
            Biomerica -> "Biomerica, Inc., Biomerica COVID-19 Antigen Rapid Test (nasopharyngeal swab)"
            Sugentech -> "Sugentech, Inc, SGTi-flex COVID-19 Ag"
            Oncosem -> "Oncosem Onkolojik Sistemler San. ve Tic. A.S., CAT"
            NanoRepro -> "NanoRepro AG, NanoRepro SARS-CoV-2 Antigen Rapid Test"
            BeijingHotgen -> "Beijing Hotgen Biotech Co., Ltd, Novel Coronavirus 2019-nCoV Antigen Test (Colloidal Gold)"
            Abbott -> "Abbott Rapid Diagnostics, Panbio Covid-19 Ag Rapid Test"
            HubeiJinjian -> "Hubei Jinjian Biology Co., Ltd, SARS-CoV-2 Antigen Test Kit"
            Prognosis -> "Prognosis Biotech, Rapid Test Ag 2019-nCov"
            GenSure -> "GenSure Biotech Inc, GenSure COVID-19 Antigen Rapid Kit"
            Bioteke -> "BIOTEKE CORPORATION (WUXI) CO., LTD, SARS-CoV-2 Antigen Test Kit (colloidal gold method)"
            Biosynex2 -> "BIOSYNEX S.A., BIOSYNEX COVID-19 Ag+ BSS"
            Quidel -> "Quidel Corporation, Sofia SARS Antigen FIA"
            Safecare1 -> "Safecare Biotech (Hangzhou) Co. Ltd, Multi-Respiratory Virus Antigen Test Kit(Swab) (Influenza A+B/ COVID-19)"
            Getein1 -> "Getein Biotech, Inc., One Step Test for SARS-CoV-2 Antigen (Colloidal Gold)"
            Healgen -> "Healgen Scientific, Coronavirus Ag Rapid Test Cassette"
            AVALUN -> "AVALUN SAS, Ksmart\u00ae SARS-COV2 Antigen Rapid Test"
            Jiangsu -> "Jiangsu Diagnostics Biotechnology Co.,Ltd., COVID-19 Antigen Rapid Test Cassette (Colloidal Gold)"
            Safecare2 -> "Safecare Biotech (Hangzhou) Co. Ltd, COVID-19 Antigen Rapid Test Kit (Swab)"
            JOYSBIO -> "JOYSBIO (Tianjin) Biotechnology Co., Ltd, SARS-CoV-2 Antigen Rapid Test Kit (Colloidal Gold)"
            XiamenWiz2 -> "Xiamen Wiz Biotech Co., Ltd, SARS-CoV-2 Antigen Rapid Test (Colloidal Gold)"
            XiamenAmonMed -> "Xiamen AmonMed Biotechnology Co., Ltd, COVID-19 Antigen Rapid Test Kit (Colloidal Gold)"
            Novatech -> "Novatech, SARS CoV-2 Antigen Rapid Test"
            HangzhouClongene3 -> "Hangzhou Clongene Biotech Co., Ltd, COVID-19/Influenza A+B Antigen Combo Rapid Test"
            GenBody -> "GenBody, Inc, Genbody COVID-19 Ag Test"
            EdinburghGenetics -> "Edinburgh Genetics Limited, ActivXpress+ COVID-19 Antigen Complete Testing Kit"
            BeijingWantai2 -> "Beijing Wantai Biological Pharmacy Enterprise Co., Ltd, WANTAI SARS-CoV-2 Ag Rapid Test (Colloidal Gold)"
            PCL1 -> "PCL Inc, PCL COVID19 Ag Rapid FIA"
            ShenzhenWatmind1 -> "Shenzhen Watmind Medical Co., Ltd, SARS-CoV-2 Ag Diagnostic Test Kit (Colloidal Gold)"
            ShenzhenWatmind2 -> "Shenzhen Watmind Medical Co., Ltd, SARS-CoV-2 Ag Diagnostic Test Kit (Immuno-fluorescence)"
            ArcDia2 -> "ArcDia International Oy Ltd, mariPOC Respi+"
            Humasis -> "Humasis, Humasis COVID-19 Ag Test"
            AssureTech2 -> "Assure Tech. (Hangzhou) Co., Ltd., ECOTEST COVID-19 Antigen Rapid Test Device"
            Triplex -> "Triplex International Biosciences (China) Co., LTD., SARS-CoV-2 Antigen Rapid Test Kit"
            BeijingJinwofu -> "Beijing Jinwofu Bioengineering Technology Co.,Ltd., Novel Coronavirus (SARS-CoV-2) Antigen Rapid Test Kit"
            AESKU -> "AESKU.DIAGNOSTICS GmbH & Co. KG, AESKU.RAPID SARS-CoV-2"
            Roche1 -> "Roche (SD BIOSENSOR), SARS-CoV-2 Rapid Antigen Test Nasal"
            JiangsuBioperfectus -> "Jiangsu Bioperfectus Technologies Co., Ltd., Novel Corona Virus (SARS-CoV-2) Ag Rapid Test Kit"
            MEXACARE -> "MEXACARE GmbH, MEXACARE COVID-19 Antigen Rapid Test"
            Asan -> "Asan Pharmaceutical CO., LTD, Asan Easy Test COVID-19 Ag"
            NalVonMinden1 -> "Nal von minden GmbH, NADAL COVID -19 Ag +Influenza A/B Test"
            HangzhouAllTest -> "Hangzhou AllTest Biotech Co., Ltd, COVID-19 Antigen Rapid Test"
            WuhanLife -> "Wuhan Life Origin Biotech Joint Stock Co., Ltd., The SARS-CoV-2 Antigen Assay Kit (Immunochromatography)"
            VivaChek -> "VivaChek Biotech (Hangzhou) Co., Ltd, VivaDiag Pro SARS-CoV-2 Ag Rapid Test"
            DIALAB -> "DIALAB GmbH, DIAQUICK COVID-19 Ag Cassette"
            AXIOM -> "AXIOM Gesellschaft f\u00fcr Diagnostica und Biochemica mbH, COVID-19 Antigen Rapid Test"
            AnhuiDeepBlue2 -> "Anhui Deep Blue Medical Technology Co., Ltd, COVID-19 (SARS-CoV-2) Antigen Test Kit (Colloidal Gold) - Nasal Swab"
            Tody -> "Tody Laboratories Int., Coronavirus (SARS-CoV 2) Antigen - Oral Fluid"
            ShenzhenLvshiyuan -> "Shenzhen Lvshiyuan Biotechnology Co., Ltd., Green Spring SARS-CoV-2 Antigen-Rapid test-Set"
            PCL2 -> "PCL Inc., PCL COVID19 Ag Gold"
            DNADiagnostic -> "DNA Diagnostic, COVID-19 Antigen Detection Kit"
            NesaporEuropa -> "NESAPOR EUROPA SL, MARESKIT"
            PrecisionBiosensor -> "Precision Biosensor, Inc, Exdia COVID-19 Ag"
            HangzhouTestsea -> "Hangzhou Testsea Biotechnology Co., Ltd, COVID-19 Antigen Test Cassette"
            AnbioXiamen -> "Anbio (Xiamen) Biotechnology Co., Ltd, Rapid COVID-19 Antigen Test(Colloidal Gold)"
            AMEDALabordiagnostik -> "AMEDA Labordiagnostik GmbH, AMP Rapid Test SARS-CoV-2 Ag"
            Getein2 -> "Getein Biotech, Inc, SARS-CoV-2 Antigen (Colloidal Gold)"
            PerGrande -> "PerGrande BioTech Development Co., Ltd., SARS-CoV-2 Antigen Detection Kit (Colloidal Gold Immunochromatographic Assay)"
            LumiraDX -> "LumiraDX, LumiraDx SARS-CoV-2 Ag Test"
            LumiQuick -> "LumiQuick Diagnostics Inc, QuickProfile COVID-19 Antigen Test"
            NanoEntek -> "NanoEntek, FREND COVID-19 Ag"
            Labnovation -> "Labnovation Technologies Inc, SARS-CoV-2 Antigen Rapid Test Kit"
            GreenCross -> "Green Cross Medical Science Corp., GENEDIA W COVID-19 Ag"
            ArcDia3 -> "ArcDia International Oy Ltd, mariPOC Quick Flu+"
            WuhanUNscience -> "Wuhan UNscience Biotechnology Co., Ltd., SARS-CoV-2 Antigen Rapid Test Kit"
            Genrui -> "Genrui Biotech Inc, SARS-CoV-2 Antigen Test Kit (Colloidal Gold)"
            BIOHIT -> "BIOHIT HealthCare (Hefei) Co., Ltd, SARS-CoV-2 Antigen Rapid Test Kit (Fluorescence Immunochromatography)"
            WuhanEasyDiagnosis -> "Wuhan EasyDiagnosis Biomedicine Co., Ltd., COVID-19 (SARS-CoV-2) Antigen Test Kit"
            AtlasLink -> "Atlas Link Technology Co., Ltd., NOVA Test\u00ae SARS-CoV-2 Antigen Rapid Test Kit (Colloidal Gold Immunochromatography)"
            NalVonMinden2 -> "Nal von minden GmbH, NADAL COVID-19 Ag Test"
            Affimedix -> "Affimedix, Inc., TestNOW\u00ae - COVID-19 Antigen Test"
            GuangzhouWondfo -> "Guangzhou Wondfo Biotech Co., Ltd, Wondfo 2019-nCoV Antigen Test (Lateral Flow Method)"
            AAZ_LMB -> "AAZ-LMB, COVID-VIRO"
            Lumigenex -> "Lumigenex (Suzhou) Co., Ltd, PocRoc\u00aeSARS-CoV-2 Antigen Rapid Test Kit (Colloidal Gold)"
            JiangsuMedomics -> "Jiangsu Medomics medical technology Co.,Ltd., SARS-CoV-2 antigen Test Kit (LFIA)"
            BioGnost -> "BioGnost Ltd, CoviGnost AG Test Device 1x20"
            XiamenBoson -> "Xiamen Boson Biotech Co. Ltd, Rapid SARS-CoV-2 Antigen Test Card"
            ZhuhaiLituo -> "Zhuhai Lituo Biotechnology Co., Ltd, COVID-19 Antigen Detection Kit (Colloidal Gold)"
            SGAMedikal2 -> "SGA Medikal, V-Chek SARS-CoV-2 Ag Rapid Test Kit (Colloidal Gold)"
            ZhejiangAndLucky -> "Zhejiang Anji Saianfu Biotech Co., Ltd, AndLucky COVID-19 Antigen Rapid Test"
            ZhejiangReOpenTest -> "Zhejiang Anji Saianfu Biotech Co., Ltd, reOpenTest COVID-19 Antigen Rapid Test"
            CerTest -> "CerTest Biotec, CerTest SARS-CoV-2 Card test"
            Hangzhou -> "Hangzhou Immuno Biotech Co.,Ltd, Immunobio SARS-CoV-2 Antigen ANTERIOR NASAL Rapid Test Kit (minimal invasive)"
            HangzhouLysun -> "HANGZHOU LYSUN BIOTECHNOLOGY CO., LTD., COVID-19 Antigen Rapid Test Device\uff08Colloidal Gold\uff09"
            ShenzhenUltraDiagnostics -> "Shenzhen Ultra-Diagnostics Biotec.Co.,Ltd, SARS-CoV-2 Antigen Test Kit"
            SDBiosensor2 -> "SD BIOSENSOR Inc, STANDARD F COVID-19 Ag FIA"
            GuangzhouDecheng -> "Guangzhou Decheng Biotechnology Co., LTD, V-CHEK, 2019-nCoV Ag Rapid Test Kit (Immunochromatography)"
            SDBiosensor3 -> "SD BIOSENSOR Inc, STANDARD Q COVID-19 Ag Test"
            Vitrosens -> "Vitrosens Biotechnology Co., Ltd, RapidFor SARS-CoV-2 Rapid Ag Test"
            ScheBo -> "ScheBo Biotech AG, ScheBo SARS CoV-2 Quick Antigen"
            BioticalHealth -> "Biotical Health S.L.U., biotical SARS-CoV-2 Ag Card"
            RapiGEN -> "RapiGEN Inc, BIOCREDIT COVID-19 Ag - SARS-CoV 2 Antigen test"
            ShenzhenMicroprofit2 -> "Shenzhen Microprofit Biotech Co., Ltd, SARS-CoV-2 Antigen Test Kit (Colloidal Gold Chromatographic Immunoassay)"
            Roche2 -> "Roche (SD BIOSENSOR), SARS-CoV-2 Rapid Antigen Test"
        }
    }

    companion object {
        fun fromValue(value: String?): DCCTestManufacturer? {
            return values().firstOrNull { it.value == value }
        }
    }
}