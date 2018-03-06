# Unlike stoke.rb (please read top of file) I am not sure yet how to best organize plotting.
# It probably should all be contained in one file, stoke.R, and follow similar conventions
# as stoke.rb, but let's leave it seperate until we know more.  
#
# To run the script...
#
# Rscript convergence.R dat/convergence.dat 5 pdf/output.pdf
#
# where convergence.dat is generated from stoke.rb.

library(ggplot2)
library(grid)
library(dplyr)
library(lubridate)
library(RColorBrewer)
library(reshape2)
library(forcats)

options <- commandArgs(trailingOnly = TRUE)
if (length(options) < 2) {
  print("Usage metric.R: [METRIC DAT FILE] [OUTPUT PDF PATH]")
  quit()
}

path  <- options[1]
outputpdf <- options[2]

#df <- read.table(paste(path, "total-metric.dat", sep="/"), sep="\t", head=T)


#df <- df[,c("policy","converge","projected")]
#mdf <- melt(df, id.var="policy")

#print(df)
pal <- brewer.pal(n=8,name="Dark2")
#colors <- c(pal[1],pal[2],pal[7],pal[8])

df <- read.table(paste(path, "stoke.dat", sep="/"), sep="\t", head=T)
mdf <- melt(df[,c("task","r0_energy","r1_energy")], id.vars="task")

plot <- 
  ggplot(data=mdf, aes(x=task,y=value,colour=variable,shape=variable)) + 
  geom_line() +
  geom_point(size=1.5) +
  #scale_x_continuous(breaks=c(0,25,50,75,100,125), limits=c(0,125)) +
  #scale_y_continuous(limits=c(40,80)) +
  scale_colour_brewer(name="Run",breaks=c("r0_energy", "r1_energy"), labels=c("1s","500ms"),palette="Set1") +
  scale_shape_manual(name="Run",breaks=c("r0_energy", "r1_energy"),labels=c("1s","500ms"),values=c(1,1,1,1,9,9,9,9)) +
  xlab("Time Interval (30 s)") +
  ylab("Energy (J)") +
  theme_minimal(base_size=16) +
  theme(legend.position="bottom") 

pdf(paste(outputpdf, "joules-sample.pdf",sep="/"),height=4)
print(plot)
