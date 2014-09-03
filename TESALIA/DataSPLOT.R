library(plyr)
library(doBy)
library(ggplot2)
library(reshape2)

raw1=read.table("splotResults10.txt", header=T,sep=";");

raw.data <- subset(raw1, select = -c(prunningPropagation,priorizationPropagation) )

data <- summaryBy(pruningTime + prioritizationTime + packagingTime ~ Name + nectc, data = raw.data, 
                  FUN = function(x) { c(mean = mean(x)) } )

data.mean <- summaryBy(pruningTime + prioritizationTime + packagingTime ~ nectc, data = raw.data,  FUN = function(x) { c(mean = mean(x)) } )
#remove 0 values because they are too constrained
#subset(data, pruningTime.mean!=0.0 & prioritizationTime.mean!=0.0 & packagingTime.mean!=0.0)
#remove the models*
data<-subset(data, substr(Name, 1, 5) != 'model') 
data<-subset(data, nectc == 0) 
data<-subset(data, pruningTime.mean!=0.0 & prioritizationTime.mean!=0.0 & packagingTime.mean!=0.0)

data.melted<-melt(data, id.vars = c("Name","nectc"))
data.melted$nectc <- as.factor(data.melted$nectc)
data.melted$nectc = with(data.melted, factor(nectc, levels = rev(levels(nectc))))

mf_labeller <- function(var, value){
  value <- as.character(value)
  if (var=="eCTC") { 
    value[value=="0"] <- "0 complex constraints"
    value[value=="2"]   <- "2 complex constraints"
    value[value=="5"]   <- "5 complex constraints"
  }
  return(value)
}
data.melted<- within(data.melted,value <-ifelse(value<1,1,value))

ggplot(data.melted, aes(Name,value, fill = as.factor(variable))) +
geom_bar(position="dodge",stat="identity",colour="black") +
scale_y_log10("Time in milliseconds") +scale_x_discrete("Model Name")+ scale_fill_grey(start = 0, end = .9,name="Testing operation",labels=c("Pruning","Prioritization","Packaging")) + theme_bw() +  theme(axis.text.x = element_text(angle = 90, hjust = 1, size = 9))  + theme(legend.position="bottom")
