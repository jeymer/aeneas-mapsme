# Unlike stoke.rb (please read top of file) I am not sure yet how to best organize plotting.
# It probably should all be contained in one file, stoke.R, and follow similar conventions
# as stoke.rb, but let's leave it seperate until we know more.  
#
# To run the script...
#
# Rscript convergence.R dat/convergence.dat 5 pdf/output.pdf
#
# where convergence.dat is generated from stoke.rb.

suppressMessages(library(ggplot2))
suppressMessages(library(grid))
suppressMessages(library(dplyr))
suppressMessages(library(lubridate))
suppressMessages(library(RColorBrewer))
suppressMessages(library(reshape2))
suppressMessages(library(forcats))

options <- commandArgs(trailingOnly = TRUE)
if (length(options) < 1) {
  print("Usage convergence.R: [SELF DIR]")
  quit()
}

datafile  <- options[1]

df <- read.table(options[1], sep="\t", head=T)

#runs <- c("o1", "o2", "o3", "o4", "s1", "s10", "s30", "s60")
#names <- c("Opt-1", "Opt-2", "Opt-3", "Opt-4", "Fixed-1s", "Fixed-10s", "Fixed-30s", "Fixed-60s")

runs <- c("o1", "o2", "o3", "o4")
names <- c("R1-32.5", "R2-32.5", "R3-32.5", "R4-35")

rewards <- c()

for (i in 1:length(runs)) {
  rewards <- append(rewards, paste(runs[i], "_reward", sep=""))
}
print(rewards)


df1 <- df[,c("task",rewards)]
#df2 <- tail(df[,c("task",rewards)],10)
mdf1 <- melt(df1, id.vars="task")
#mdf2 <- melt(df2, id.vars="task")

#shapes <- seq(from=1,to=strtoi(numruns),by=1)

#plot <- 
#  ggplot(data=mdf1, aes(x=task,y=value,colour=variable,shape=variable)) + 
#  geom_line() +
#  geom_point() +
#  scale_colour_brewer(name="Run",breaks=rewards, labels=names,palette="Set1") +
#  scale_shape_manual(name="Run",breaks=rewards,labels=names,values=c(1,1,1,1,9,9,9,9)) +
#  xlab("Interactions") +
#  ylab("Power (W)") +
#  theme_minimal(base_size=16) +
#  theme(legend.position="bottom")

#pdf("full.pdf",height=4,width=12)
#print(plot)

plot <- 
  ggplot(data=mdf1, aes(x=task,y=value,colour=variable,shape=variable)) + 
  geom_line(size=0.20) +
  geom_point(size=3.5) +
  scale_colour_brewer(name="Run",breaks=rewards, labels=names,palette="Set1") +
  scale_shape_manual(name="Run",breaks=rewards,labels=names,values=c(1,1,1,1,9,9,9,9)) +
  xlab("Interactions") +
  ylab("Power Difference (W)") +
  theme_minimal(base_size=16) +
  theme(legend.position="bottom")

pdf("last10.pdf",height=4,width=8)
print(plot)
