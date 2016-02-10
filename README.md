# GEAT 

##What is it?
GEAT is a **G**enomic **E**vents **A**nalysis **T**ools developed based on Java platform.

##Download & Install

```$ git clone "https://github.com/geatools/geat.git/```

##Requirements / Dependencies##
- **Java**    
- **BLAST+**  *We employ BLAST+ to performe two short seq alignment. Go to the [BLAST download page](http://blast.ncbi.nlm.nih.gov/Blast.cgi?CMD=Web&PAGE_TYPE=BlastDocs&DOC_TYPE=Download)*
- **Perl**    *The fastq seq QC check is performed based on [Prinseq](http://prinseq.sourceforge.net/) which is developed by [Perl](https://www.perl.org/).* 

## Execution/Command

java -Xmx16G -jar GEAT.jar [options]