suppressMessages(library(ggplot2))
suppressMessages(library(grid))
suppressMessages(library(dplyr))
suppressMessages(library(lubridate))
suppressMessages(library(RColorBrewer))
suppressMessages(library(reshape2))
suppressMessages(library(scales))

options <- commandArgs(trailingOnly = TRUE)

mainpath <- options[1]

profiles <- c("3arm")

total_rewards   <- c("oracle_total_reward", "vbde50_total_reward", "static2_total_reward", "pessimist_total_reward")
#regrets   <- c("task","vbde50_regret")

pal <- brewer.pal(n=8,name="Dark2")
colors <- c(pal[1],pal[2],pal[7],pal[8])

breaksVEC <- c(1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000)

  makeFUN <- function(x){
    transformedVEC <- c()
    for (v in breaksVEC) {
      transformedVEC <- append(transformedVEC, ((v / 30000) * 100) / 55.83 * 60)
    }
    transformedVEC
  }

  scaleFUN <- function(x) sprintf("%.1f", x)


if (TRUE) {
  reward_dta <- data.frame() 
  #regret_dta <- data.frame() 

  interactions_file <- paste("total-machine.dat", sep="")

  for (i in 1:length(profiles)) {
    #path <- paste(mainpath, profiles[i], interactions_file, sep="/")
    path <- paste(mainpath, interactions_file, sep="/")
    df <- read.table(path, sep="\t", head=T)

    df_reward_groups <- df[,total_rewards]
    df_reward_groups$profile <- profiles[i]
    reward_dta <- rbind(reward_dta, df_reward_groups)

    #df_regret_groups <- tail(df[,regrets])
    #df_regret_groups$profile <- profiles[i]
    #regret_dta <- rbind(regret_dta, df_regret_groups)
  }

  reward_dta$profile <- factor(reward_dta$profile, levels=c("3arm"))
  #regret_dta <- within(regret_dta, rm(task))
  #regret_dta$profile <- factor(regret_dta$profile, levels=c("3arm"))

  mdf_reward <- melt(reward_dta, id.vars="profile")
  #mdf_regret <- melt(regret_dta, id.vars="profile")

  plot <- 
    ggplot() +
    geom_bar(data=mdf_reward, aes(x=profile,y=value,fill=variable), stat="identity", position="dodge") + 
    scale_y_continuous(breaks=breaksVEC,sec.axis=sec_axis(~./30000*100/55.83*60, name="Battery Drain Rate (%/hr)",breaks=makeFUN,labels=scaleFUN)) +

    scale_x_discrete(
      breaks=c("3arm"), 
      labels=c("3-Arm")) +
    scale_fill_manual(name="Policy", 
                      breaks=c("oracle_total_reward", "vbde50_total_reward", "static2_total_reward","pessimist_total_reward"), 
                      labels=c("Oracle", "VBDE-5.0", "Profile-2", "Pessimist"),
                      values=colors) +
    xlab("Application Profile") +
    ylab("Total Energy (J)") +
    theme_minimal(base_size=16) +
    theme(legend.position="bottom")

  outputfile <- paste("reward-profile",sep="-")
  outputpath <- paste(mainpath,paste(outputfile,".pdf",sep=""),sep="/")
  print(outputpath)

  pdf(outputpath,height=4)
  print(plot)

}

bks <- c("r0","r1")
  clr <- c(pal[1],pal[1])

  for (i in 2:7) {
    bks <- append(bks, paste("r",i,sep=""))
    clr <- append(clr,pal[2])
  }

  for (i in 8:13) {
    bks <- append(bks, paste("r",i,sep=""))
    clr <- append(clr,pal[3])
  }

  bks <- append(bks,"r14")
  bks <- append(bks,"r15")
  clr <- append(clr,pal[4])
  clr <- append(clr,pal[4])

  print(bks)
  print(clr)


df <- read.table(paste(mainpath,"totals.dat", sep="/"), head=T)
df$policy <- factor(df$policy, c("oracle", "vbde50", "static2","pessimist"))
df$run <- factor(df$run, bks)
plot <- 
  ggplot(data=df, aes(x=policy,y=joules,fill=run)) +
  geom_bar(stat="identity", position=position_dodge2(preserve="single")) +

  scale_x_discrete(name="Policy", 
                    breaks=c("oracle", "vbde50", "static2","pessimist"), 
                    labels=c("Oracle", "VBDE-5.0", "Profile-2", "Pessimist")) +
  scale_fill_manual(name="Run", breaks=bks, values=clr) +
  xlab("Application Profile") +
  ylab("Total Energy (J)") +
  theme_minimal(base_size=16) +
  theme(legend.position="none")

outputfile <- paste("reward-profile-runs",sep="-")
outputpath <- paste(mainpath,paste(outputfile,".pdf",sep=""),sep="/")
print(outputpath)

pdf(outputpath,height=3)
print(plot)


#plot2 <- 
#  ggplot() +
#  geom_bar(data=mdf_regret, aes(x=profile,y=value,fill=variable), stat="identity", position="dodge") + 
#  scale_y_continuous(labels=comma) +
#  scale_x_discrete(
#    breaks=c("3arm"), 
#    labels=c("3-Arm")) +
#  scale_fill_brewer(
#    name="Policy", 
#    breaks=c("vbde50_regret"), 
#    labels=c("VBDE-5.0"),
#    palette="Dark2") +
#  xlab("Application Profile") +
#  ylab("Total Regret") +
#  theme_minimal(base_size=16) +
#  theme(legend.position="bottom")

#outputfile <- paste("regret-profile",sep="-")
#outputpath <- paste(mainpath,paste(outputfile,".pdf",sep=""),sep="/")
#print(outputpath)

#pdf(outputpath)
#print(plot2)

