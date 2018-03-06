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

options <- commandArgs(trailingOnly = TRUE)
if (length(options) < 3) {
  print("Usage metric.R: [METRIC DAT FILE] [OUTPUT PDF PATH] [IDEAL]")
  quit()
}

path  <- options[1]
outputpdf <- options[2]
quality   <- as.numeric(options[3])

df <- read.table(paste(path, "total-metric.dat", sep="/"), sep="\t", head=T)

print(df)
pal <- brewer.pal(n=8,name="Dark2")
#colors <- c(pal[1],pal[2],pal[7],pal[8])

#plot1 <- 
#  ggplot(data=df, aes(x=count,y=metric,shape=name)) +
#  geom_line(size=0.25) +
#  geom_point(size=4,aes(color=meet)) +
#  xlab("Interaction") +
#  ylab("Energy") +
#  theme_minimal(base_size=16) +
#  theme(legend.position="right") +
#  geom_hline(yintercept=quality, colour="red4", linetype = "longdash") 
#
#pdf(paste(outputpdf,"full-binary.pdf",sep="/"),height=5,width=12)
#
#print(plot1)
#
#plot1 <- 
#  ggplot(data=df2, aes(x=count,y=metric,shape=name)) +
#  geom_line(size=0.25) +
#  geom_point(size=4,aes(color=meet)) +
#  xlab("Interaction") +
#  ylab("Energy") +
#  theme_minimal(base_size=16) +
#  theme(legend.position="bottom") +
#  geom_hline(yintercept=quality, colour="red4", linetype = "longdash") 
#
#pdf(paste(outputpdf,"last-binary.pdf",sep="/"),height=5,width=12)
#
#print(plot1)
#
#plot1 <- 
#  ggplot(data=df, aes(x=count,y=metric,shape=name,color=name)) +
#  geom_line(size=0.5) +
#  geom_point(size=3) +
#  scale_colour_brewer(name="Run",breaks=c("oracle", "l1","l2","pessimist"), labels=c("Oracle", "Search-1", "Search-2", "Pessimist"),palette="Set1") +
#  scale_shape_manual(name="Run",breaks=c("oracle", "l1","l2","pessimist"),labels=c("Oracle", "Search-1", "Search-2", "Pessimist"),values=c(1,2,3,4)) +
#  xlab("Count") +
#  ylab("Accuracy") +
#  theme_minimal(base_size=16) +
#  theme(legend.position="bottom") +
#  geom_hline(yintercept=quality, colour="red4", linetype = "longdash") 
#
#pdf(paste(outputpdf,"full-color.pdf",sep="/"),height=5,width=12)
#
#print(plot1)
#
#plot1 <- 
#  ggplot(data=df2, aes(x=count,y=metric,shape=name,color=name)) +
#  geom_line(size=0.5) +
#  geom_point(size=3) +
#  scale_colour_brewer(name="Run",breaks=c("oracle", "l1","l2","pessimist"), labels=c("Oracle", "Search-1", "Search-2", "Pessimist"),palette="Set1") +
#  scale_shape_manual(name="Run",breaks=c("oracle", "l1","l2","pessimist"),labels=c("Oracle", "Search-1", "Search-2", "Pessimist"),values=c(1,2,3,4)) +
#  xlab("Interactions") +
#  ylab("Energy") +
#  theme_minimal(base_size=16) +
#  theme(legend.position="bottom") +
#  geom_hline(yintercept=quality, colour="red4", linetype = "longdash") 
#
#pdf(paste(outputpdf,"last-color.pdf",sep="/"),height=5,width=12)
#
#print(plot1)
#

ideal <- quality * 100
#names <- c("oracle", "r1", "r2", "r3", "r4", "r5", "r6", "pessimist")
#labels <- c("10s", "Run-1", "Run-2", "Run-3", "Run-4", "Run-5", "Run-6", "1s")

names <- c("oracle", "r1", "r2", "r3", "r4", "pessimist")
labels <- c("10s", "Run-1", "Run-2", "Run-3", "Run-4", "1s")

#names <- c("oracle", "r1", "r2", "r3", "pessimist")
#labels <- c("10s", "Run-1", "Run-2", "Run-3", "1s")


df$policy <- factor(df$policy, levels=names)

breaksVEC <- c(1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000)

makeFUN <- function(x){
  transformedVEC <- c()
  for (v in breaksVEC) {
    transformedVEC <- append(transformedVEC, ((v / 30000) * 100) / 55.83 * 60)
  }
  transformedVEC
}

scaleFUN <- function(x) sprintf("%.1f", x)


plot <- 
  ggplot() +
  geom_bar(data=df, aes(x=policy,y=energy20,fill=policy), stat="identity") + 
  scale_y_continuous(breaks=breaksVEC,sec.axis=sec_axis(~./30000*100/55.83*60, name="Battery Drain Rate (%/hr)",breaks=makeFUN,labels=scaleFUN)) +
  scale_x_discrete(breaks=names, labels=labels) +
  scale_fill_manual(name="Policy", breaks=names, labels=labels, values=pal) +
  xlab("Run") +
  ylab("Energy (J)") +
  theme_minimal(base_size=16) +
  theme(legend.position="none") +
  geom_hline(yintercept=ideal, colour="red4", linetype = "longdash") 

pdf(paste(outputpdf, "total-consumed.pdf",sep="/"),height=4)
print(plot)

plot <- 
  ggplot() +
  geom_bar(data=df, aes(x=policy,y=avgenergy,fill=policy), stat="identity") + 
  scale_x_discrete(breaks=names, labels=labels) +
  scale_fill_manual(name="Policy", breaks=names, labels=labels, values=pal) +
  xlab("Run") +
  ylab("Average Energy (J)") +
  theme_minimal(base_size=16) +
  theme(legend.position="none") +
  geom_hline(yintercept=quality, colour="red4", linetype = "longdash") 

pdf(paste(outputpdf, "avg-consumed.pdf",sep="/"),height=4)
print(plot)


