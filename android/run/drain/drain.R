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
if (length(options) < 2) {
  print("Usage drain.R: [DRAIN DAT FILE] [OUTPUT PDF PATH] [IDEAL]")
  quit()
}

path  <- options[1]
outputpdf <- options[2]
#quality   <- as.numeric(options[3])

df <- read.table(paste(path, "drain.dat", sep="/"),sep="\t", head=T)
#df3 <- read.table(paste(path, "total-drain.dat", sep="/"), sep="\t", head=T)

pal <- brewer.pal(n=8,name="Dark2")
#colors <- c(pal[1],pal[2],pal[7],pal[8])

names=c("drain-1", "drain-2", "drain-3", "drain-4")
labels=c("Phone-1", "Phone-2", "Phone-3", "Phone-4")

df$name <- factor(df$name, levels=names)
#df3$name <- factor(df3$name, levels=names)

plot1 <- 
  ggplot(data=df, aes(x=interaction,y=drain,color=name,shape=name)) +
  geom_line() +
  geom_point(data=subset(df, interaction %% 10 == 0),size=3) +
  scale_colour_manual(name="Run",breaks=names,labels=labels,values=pal) +
  scale_shape_manual(name="Run",breaks=names,labels=labels,values=c(1,2,3,4)) +
  xlab("Interaction") +
  ylab("Battery Percentage") +
  theme_minimal(base_size=20) +
  theme(legend.position="bottom")

pdf(paste(outputpdf,"drain.pdf",sep="/"),height=4,width=8)
#
print(plot1)

#plot <- 
#  ggplot() +
#  geom_bar(data=df3, aes(x=name,y=joules,fill=name), stat="identity") + 
  #scale_y_continuous(labels=comma) +
#  scale_x_discrete(name="Run",
#                   breaks=names,
#                   labels=labels) +
#  scale_fill_manual(name="Run", 
#                    breaks=names,
#                    labels=labels,
#                    values=pal) +
#  xlab("Run") +
#  ylab("Energy (J)") +
#  theme_minimal(base_size=16) +
#  theme(legend.position="bottom")
#
#pdf(paste(outputpdf, "joules.pdf",sep="/"),height=4,width=4)
#print(plot)


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

#ideal <- quality * 20

#df3$policy <- factor(df3$policy, levels=c("oracle", "l1", "l2", "l3", "pessimist"))

#plot <- 
#  ggplot() +
#  geom_bar(data=df3, aes(x=policy,y=energy,fill=policy), stat="identity") + 
#  #scale_y_continuous(labels=comma) +
#  scale_x_discrete(breaks=c("oracle", "l1", "l2", "l3", "pessimist"), 
#                   labels=c("Oracle", "Search-1", "Search-2", "Search-3", "Pessimist")) +
#  scale_fill_manual(name="Policy", 
#                    breaks=c("oracle", "l1", "l2", "l3", "pessimist"), 
#                    labels=c("Oracle", "Search-1", "Search-2", "Search-3", "Pessimist"),
#                    values=pal) +
#  xlab("Run") +
#  ylab("Energy (J)") +
#  theme_minimal(base_size=16) +
#  theme(legend.position="none") +
#  geom_hline(yintercept=ideal, colour="red4", linetype = "longdash") 
#
#pdf(paste(outputpdf, "last-consumed.pdf",sep="/"))
#print(plot)


