library(plyr)
library(doBy)
library(ggplot2)
library(reshape2)
#read data

raw1=read.table("prupri.txt", header=T,sep=";");
#remove the 5000 as they are not executed yet
raw1=subset(x=raw1,subset = NoF < 5000)
raw2=read.table("pack2000.txt", header=T,sep=";");

raw.data=join(raw1,raw2, type="left");

#raw.data = read.table("output/experiments/experiment2.txt", header=T,sep=";")
#remove unused colums
raw.data <- subset(raw.data, select = -c(Name,pruProp,priProp) )

data <- summaryBy(pruTime + priTime + packTime ~ NoF + CTC + eCTC, data = raw.data, 
		FUN = function(x) { c(mean = mean(x)) } )
data.min <- summaryBy(pruTime + priTime + packTime ~ NoF + CTC + eCTC, data = raw.data, 
                  FUN = function(x) { c(min=min(x)) } )
data.max <- summaryBy(pruTime + priTime + packTime ~ NoF + CTC + eCTC, data = raw.data, 
                         FUN = function(x) { c(max=max(x)) } )
# 		If we want to add other values
#		FUN = function(x) { c(m = mean(x), s = sd(x)) } ) 

#muestra la differencia entre el numero de extended ctc y el impacto para una
qplot(reorder(NoF,as.numeric(NoF)),pruTime.mean,data = data, geom = "bar", fill = as.factor(eCTC), stat="identity") +
		facet_grid(. ~ CTC) + scale_y_log10("Time in milliseconds")  + scale_x_discrete("Number of Features")+
		theme(axis.text.x = element_text(angle = 90, hjust = 1, size = 9, colour = "grey50")) + scale_fill_discrete(name="Number of\nextended\nconstraints",breaks=c("0","2","5"))

data.melted<-melt(data, id.vars = c("NoF","CTC","eCTC"))
data.melted.min<-melt(data.min, id.vars = c("NoF","CTC","eCTC"))
data.melted.max<-melt(data.max, id.vars = c("NoF","CTC","eCTC"))



qplot(reorder(NoF,as.numeric(NoF)),value,data = data.melted, geom = "bar", fill = as.factor(variable), stat="identity") +
		facet_grid(. ~ CTC + eCTC) + scale_y_log10("Time in milliseconds")  + scale_x_discrete("Number of Features")+
		theme(axis.text.x = element_text(angle = 90, hjust = 1, size = 9, colour = "grey50"))
#perfectplot		
qplot(reorder(NoF,as.numeric(NoF)),value,data = data.melted, geom = "bar", fill = as.factor(variable), stat="identity") +
		facet_grid( CTC ~ eCTC) + scale_y_log10("Time in milliseconds")  + scale_x_discrete("Number of Features")+
		theme(axis.text.x = element_text(angle = 90, hjust = 1, size = 9, colour = "grey50"))  + scale_fill_discrete(name="Number of\nextended\nconstraints")
		
			
#eCTC and ctc is a string for themming;)
#data.melted$CTC<-as.character(data.melted$CTC)
#data.melted$CTC2<-paste(data.melted$CTC, "% of cross-tree constraints",sep=" ")

#data.melted$eCTC<-as.character(data.melted$eCTC)
#data.melted$eCTC2<-paste(data.melted$eCTC, "complex constraints",sep=" ")


#reverse the order of the eCTC so it shows the maximum at right top
data.melted$eCTC <- as.factor(data.melted$eCTC)
data.melted$eCTC = with(data.melted, factor(eCTC, levels = rev(levels(eCTC))))

data.melted.min$eCTC <- as.factor(data.melted.min$eCTC)
data.melted.min$eCTC = with(data.melted.min, factor(eCTC, levels = rev(levels(eCTC))))

data.melted.max$eCTC <- as.factor(data.melted.max$eCTC)
data.melted.max$eCTC = with(data.melted.max, factor(eCTC, levels = rev(levels(eCTC))))


mf_labeller <- function(var, value){
    value <- as.character(value)
    if (var=="eCTC") { 
        value[value=="0"] <- "0 complex constraints"
        value[value=="2"]   <- "2 complex constraints"
        value[value=="5"]   <- "5 complex constraints"
    }
    if (var=="CTC") { 
        value[value=="5"] <- "0% cross-tree constraints"
        value[value=="10"]   <- "10% cross-tree constraints"
        value[value=="15"]   <- "15% cross-tree constraints"
    }
    return(value)
}

#Stacked
qplot(reorder(NoF,as.numeric(NoF)),value,data = data.melted, geom = "bar", fill = as.factor(variable), stat="identity") +
		facet_grid( eCTC ~CTC,labeller=mf_labeller ) + scale_y_log10("Time in milliseconds")  + scale_x_discrete("Number of Features") + scale_fill_grey(start = 0, end = .9,name="Number of\nextended\nconstraints",labels=c("Pruning","Prioritization","Packaging")) + theme_bw() +	theme(axis.text.x = element_text(angle = 90, hjust = 1, size = 9))  + theme(legend.position="bottom")

# ggplot version 

qplot(data.melted, aes(reorder(NoF,as.numeric(NoF)),value, fill = as.factor(variable))) + geom_bar(stat="identity") +
		facet_grid( eCTC ~CTC,labeller=mf_labeller ) + scale_y_log10("Time in milliseconds")  + scale_x_discrete("Number of Features") + scale_fill_grey(start = 0, end = .9,name="Number of\nextended\nconstraints",labels=c("Pruning","Prioritization","Packaging")) + theme_bw() +	theme(axis.text.x = element_text(angle = 90, hjust = 1, size = 9))  + theme(legend.position="bottom")
		
#non stacked
qplot(reorder(NoF,as.numeric(NoF)),value,data = data.melted, fill = as.factor(variable), stat="identity") +
+ 		facet_grid( eCTC ~CTC,labeller=mf_labeller ) + scale_y_log10("Time in milliseconds")  + scale_x_discrete("Number of Features") + scale_fill_grey(start = 0, end = .9,name="Number of\nextended\nconstraints",labels=c("Pruning","Prioritization","Packaging")) + theme_bw() +	theme(axis.text.x = element_text(angle = 90, hjust = 1, size = 9))  + theme(legend.position="bottom") + geom_bar(position="dodge",stat="identity")

# this will need to logaritmic
data.melted<- within(data.melted,value <-ifelse(value<1,1,value))

data.melted.min<- within(data.melted.min,value <-ifelse(value<1,1,value))

data.melted.max<- within(data.melted.max,value <-ifelse(value<1,1,value))

# ggplot version 

ggplot(data.melted, aes(reorder(NoF,as.numeric(NoF)),value, fill = as.factor(variable))) + geom_bar(position="dodge",stat="identity",colour="black") +
		facet_grid( eCTC ~CTC,labeller=mf_labeller ) + scale_y_log10("Time in milliseconds")  + scale_x_discrete("Number of Features") + 
  scale_fill_grey(start = 0, end = .9,name="Testing operation",labels=c("Pruning","Prioritization","Packaging")) +  theme_bw() +	
  theme(axis.text.x = element_text(angle = 90, hjust = 1, size = 9))  +   theme(legend.position="bottom")  

#journal
ggplot(data.melted, aes(reorder(NoF,as.numeric(NoF)),value, fill = as.factor(variable))) + geom_bar(position="dodge",stat="identity",colour="black") +
  facet_grid( eCTC ~CTC,labeller=mf_labeller ) + scale_y_log10("Time in milliseconds")    +
  geom_point(data=data.melted.min,position=position_dodge(width=1),size=1) + 
  geom_point(data=data.melted.max,position=position_dodge(width=1),size=1) + scale_x_discrete("Number of Features") +  theme_bw()+
  scale_fill_grey(start = 0.3, end = 1,name="Testing operation",labels=c("Pruning","Prioritization","Packaging"),breaks=c("pruTime.mean","priTime.mean","packTime.mean")) +   
  theme(axis.text.x = element_text(angle = 90, hjust = 1, size = 9))  +   theme(legend.position="bottom") 

