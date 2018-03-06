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

ideal <- ((quality / 100) * 30000) / (60 * 60) * (32.5*120)
print(ideal)

#names <- c("oracle", "r1", "r2", "r3", "r4", "r5", "r6", "pessimist")
#labels <- c("10s", "Run-1", "Run-2", "Run-3", "Run-4", "Run-5", "Run-6", "1s")

names <- c("oracle", "r1", "r2", "r3", "r4", "pessimist")
labels <- c("10s", "Run-1", "Run-2", "Run-3", "Run-4", "1s")


df$policy <- factor(df$policy, levels=names)


breaksVEC <- c(1000,2000,3000,4000,5000, 6000, 7000, 8000, 9000, 10000)

makeFUN <- function(x){
  transformedVEC <- c()
  for (v in breaksVEC) {
    transformedVEC <- append(transformedVEC, ((v / 30000) * 100) / (120*32.5) * (60 *60))
  }
  transformedVEC
}

scaleFUN <- function(x) sprintf("%.1f", x)

plot <- 
  ggplot() +
  geom_bar(data=df, aes(x=policy,y=total,fill=policy), stat="identity") + 
  scale_y_continuous(breaks=breaksVEC,sec.axis=sec_axis(~./30000*100/(120*32.5)*(60*60), name="Drain Rate (%/hr)",breaks=makeFUN,labels=scaleFUN)) +
  coord_cartesian(ylim=c(0,10000)) +
  #scale_y_continuous(labels=comma) +
  scale_x_discrete(breaks=names, labels=labels) +
  scale_fill_manual(name="Policy", breaks=names, labels=labels, values=pal) +
  xlab("Run") +
  ylab("Energy (J)") +
  theme_minimal(base_size=16) +
  theme(legend.position="none") +
  geom_hline(yintercept=ideal, colour="red4", linetype = "longdash") 

pdf(paste(outputpdf, "total-consumed.pdf",sep="/"),height=3)
print(plot)

#plot <- 
#  ggplot() +
#  geom_bar(data=df, aes(x=policy,y=avgenergy,fill=policy), stat="identity") + 
#  scale_x_discrete(breaks=names, labels=labels) +
#  scale_fill_manual(name="Policy", breaks=names, labels=labels, values=pal) +
#  xlab("Run") +
#  ylab("Average Energy (J)") +
#  theme_minimal(base_size=16) +
#  theme(legend.position="none") +
#  geom_hline(yintercept=quality, colour="red4", linetype = "longdash") 
#
#pdf(paste(outputpdf, "avg-consumed.pdf",sep="/"),height=4)
#print(plot)

df <- read.table(paste(path, "stoke.dat", sep="/"), sep="\t", head=T)
#mdf <- melt(df[,c("task","r0_energy","r1_energy")], id.vars="task")
#mdf <- melt(df[,c("task","r0_energy")], id.vars="task")

#plot <- 
#  ggplot(data=mdf, aes(x=task,y=value,colour=variable,shape=variable)) + 
#  geom_line() +
#  geom_point(size=1.5) +
#  xlab("Interactions") +
#  ylab("Energy (J)") +
#  theme_minimal(base_size=16) +
#  theme(legend.position="bottom") +
#  geom_hline(yintercept=quality, colour="red4", linetype = "longdash") 

#pdf(paste(outputpdf, "joules-time.pdf",sep="/"),height=4,width=12)
#print(plot)

#for (i in c("0", "1")) {
for (i in c("0", "1", "2", "3")) {

  ys = c(paste("r", i, "_rate", sep=""), paste("r", i, "_avgrate", sep=""), paste("r", i, "_rollrate",sep=""))
  bks = ys
  lbl = c("Rate", "Avg", "Roll")

  mdf <- melt(df[,c("task",ys)], id.vars="task")

  plot <- 
    ggplot(data=mdf, aes(x=task,y=value,colour=variable,shape=variable)) + 
    geom_line() +
    geom_point(size=1.5) +
    scale_color_manual(name="Run", breaks=bks, labels=lbl,values=c("red3", "blue3", "green3")) +
    scale_shape_manual(name="Run", breaks=bks, labels=lbl,values=c(1,2,3)) +
    xlab("Interactions") +
    ylab("Drain Rate (%/hr)") +
    theme_minimal(base_size=16) +
    theme(legend.position="bottom") +
    geom_hline(yintercept=quality, colour="black", linetype = "longdash") +
    #geom_vline(xintercept=c(34,45,59,70), colour="red3", linetype = "longdash") +
    #geom_vline(xintercept=c(17,28,55), colour="blue3", linetype = "longdash") 
    #geom_vline(xintercept=c(34,45,59,109,120), colour="red3", linetype = "longdash") +
    #geom_vline(xintercept=c(20,34,51,88), colour="blue3", linetype = "longdash") 

  pdf(paste(outputpdf, paste(i, "-rate-time.pdf", sep=""),sep="/"),height=3,width=8)
  print(plot)
}
