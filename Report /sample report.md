 

 

**An Intelligent System for Classifying Reliable and Unreliable News**

 

**Name                                            	   ID**

   Md. Ashiqur Rahman                                            2020-1-60-173

   Md. Mohshiu Islam Khan                               	  2019-3-60-091

   Mohammad Tahsin Kamal                              	  2019-1-60-184

   	Md. Razzak Al Fahim              	                          2020-1-60-037

 

                                            	**Supervised By:**  
                                 	Rashedul Amin Tuhin,

          	Senior Lecturer, 

          	Department of Computer Science and Engineering

                                	East West University

 

 

 

 

 

 

**Department of Computer Science and Engineering**

 **East  West  University**

**Dhaka-1212,  Bangladesh**

 

 

**02 January, 2024**

 

 

# **Declaration**

 

 

We, Md. Ashiqur Rahman, Md. Mohshiu Islam Khan, Mohammad Tahsin Kamal, Md. Razzak Al Fahim, hereby, declare that the work presented in this capstone project report is the outcome of the investigation performed by us under the supervision of Rashedul Amin Tuhin, Senior Lecturer, Department of Computer Science and engineering, East West University. We also declare that no part of this project has been or is being submitted elsewhere for the award of any degree or diploma, except for publication.  
 

                	Countersigned                                                                	Signature

 

. . . . . . . . . . . . . . . . . . . . . . . .                                    	. . . . . . . . . . . . . . . . . . . . . . . .  
 

	Rashedul Amin Tuhin                                       	       	Md. Ashiqur Rahman

     	**Supervisor**                                                        	 	2020-1-60-173  
                                                                                      Signature

. . . . . . . . . . . . . . . . . . . . . . . .  
 

                                                        Md. Mohshiu Islam Khan

                                                                                            	  	2019-3-60-091

 

                                                                                                               Signature

   
. . . . . . . . . . . . . . . . . . . . . . . .  
                                                                                                             Mohammad Tahsin Kamal  
   
      	                                                                                                  2019-1-60-184  
 

Signature

   
   
. . . . . . . . . . . . . . . . . . . . . . . .  
 

                                                                                                             Md. Razzak Al Fahim  
   
                                        	2020-1-60-037

 

 

# **Letter of Acceptance**

 

 

 

 

 

The capstone project report entitled " **An Intelligent System for Classifying Reliable and Unreliable News**" is submitted by Md. Ashiqur Rahman, Md. Mohshiu Islam Khan, Mohammad Tahsin Kamal, Md. Razzak Al Fahim to the Department of Computer Science and Engineering, East West University, Dhaka, Bangladesh is accepted for the partial fulfillment of the requirement for the degree of Bachelor of Science in Computer Science and Engineering on ( 	/     /     	).

 

Board of Examiners

1\.                                                                                     	

|  |  |  |  |
| :---- | :---- | :---- | :---- |
|  |  |  |  |
|  |  |  |  |

Senior Lecturer  
Department of Computer Science and Engineering  
East West University  
   
2\.                                                                                     	

|  |  |
| :---- | :---- |
|  |  |

|  Dr. Maheen Islam   |
| :---- |

 

Associate Professor  
Department of Computer Science and Engineering  
East West University

 

#                                                                               	  **Abstract**

 

 

Detecting unreliable news has become a major necessity in today’s world because of social and political unrest in the modern world due to the vast spread of false news with misuse of online news portals and social medias. Many researchers and  engineers are trying to solve this problem using technology like Machine Learning and Deep Learning. These domains have become rich and efficient in detecting various types of anomalies related to the real world problems. We also have tried to solve the fake news problem using these technologies. Machine Learning and Deep Learning algorithms like Logistic Regression, SVM, LSTM, RNN. Also various Natural Language Processing (NLP) techniques have been used in the Welfake Dataset. Although Deep Learning is the latest conquering domain in this field, in our project SVM which is a  Machine Learning Algorithm had the best result.

 

 

## **Acknowledgments**

 

   
 

As it is true for everyone, we have also arrived at this point of achieving a goal in our life through various interactions with and help from other people. However, written words are often elusive and harbour diverse interpretations even in one’s mother language. Therefore, we would not like to make eﬀorts to ﬁnd best words to express our thankfulness other than simply listing those people who have contributed to this thesis itself in an essential way. This work was carried out in the Department of Computer Science and Engineering at East West University, Bangladesh.  
We would first like to thank God Almighty from the bottom of our hearts for all of His blessings. We also want to express our gratitude to Mr Rashedul Amin Tuhin, our supervisor, who provided us with this chance and introduced us to the field of image classification. Without him, this work would not have been feasible. His inspirational words, perceptive advice, and unwavering support throughout our B.Sc. program were both greatly appreciated and indispensable. We would strive to imitate him if we ever had the chance as it is an important lesson, we have learned about his capacity to confuse us to the point where we can finally accurately answer our own query.  
We would like to thank Mr Md. Mohsin Uddin, Senior Lecturer at East West University for his excellent collaboration and guidelines throughout the whole capstone project. There are numerous other people too who have shown us their constant support and friendship in various ways, directly or indirectly related to our academic life. We will remember them in our heart and hope to ﬁnd a more appropriate place to acknowledge them in the future.

 

 

Acknowledgments                                                                                                              	iii

 

 

                                                                                                                      	Md. Ashiqur Rahman

                                                                                                                         	        Md. Mohshiu Islam Khan          	                                                                    	  	

                                                                                                                           	             Mohammad Tahsin Kamal  
   
   
                                                                                                                                       	Md. Razzak Al Fahim  
   
                                        	

 

 

 

 

 

**Table of Contents**

 

**Table of Contents                                                                                                                                        	        	 i**

**List of Figures                                                                                                                                             	        	iii**

Declaration. 4

Letter of Acceptance. 5

Abstract 6

Acknowledgments. 7

Chapter 1\. 11

1.1 Motivation. 12

1.2 Business Model Canvas. 13

1.3 Organization of this Capstone Report 14

Chapter 2\. 15

2.1 Background. 15

2.2 Related Works. 15

Chapter 3\. 17

Chapter 4\. 21

Chapter 5\. 22

5.1     	Use Case Diagram.. 22

5.2     	 Activity Diagram (Swimlane). 23

5.3     	 Class Diagram.. 24

5.4     	 Component Diagram.. 25

5.5     	 Sequence Diagram.. 26

5.6     	 Data Flow Diagram.. 27

5.7     	 Deployment Diagram.. 28

5.8     	 User Interface. 29

Chapter 6\. 30

6.1 Materials: 30

6.1.1 Data Collection: 30

6.1.2 Dataset Exploration: 30

6.1.3 Dataset Cleaning: 31

6.2 Method: 31

6.2.1 Proposed Model: 31

6.2.2 Design/Framework: 32

6.2.3 Algorithm/ Model Formulation. 33

Chapter 7\. 34

7.1 Obtained Results. 34

7.2 In depth Result Analysis. 36

7.3 Software Cost Analysis. 38

Chapter 8\. 42

References. 44

Appendix. 45

 

 

 

 

 

 

 

**List of Figures**

 

 

 

 

**1.2   	Business Model Canvas                                                                                                    	5**

**5.1   	Use Case Diagram                                                                                                             	16**

**5.2   	Activity Diagram                                                                                                              	17**

**5.3   	Class Diagram                                                                                                                   	18**

**5.4   	Component Diagram                                                                                                        	19**

**5.5   	Sequence Diagram                                                                                                            	20**

**5.6   	Data Flow Diagram                                                                                                          	21**

**5.7   	Deployment Diagram                                                                                                       	22**

**5.8   	User Interface                                                                                                                    	23**

**6.1.1	Confusion Matrix for Logistic Regression                                                                     	19**

**6.1.2	Confusion Matrix for SVM                                                                                             	20**

 

 

 

 

 

 

 

 

 

 

 

 

#  

# **Chapter 1**

 

**Introduction**

 

History of newspapers and serving news to general people via different media is not a new thing. As a result, even unreliable or fake news is not a very new thing in the world of media. But, recently due to social media and the internet, unreliable news has become a major headache for both the general public and news publishers.  One of the common examples of what fake news can do is 2016 US election fake news stories. The fabricated stories were so spread that some people think it led to the decisive part of electing the president. Facebook and Twitter played a massive role in the rolling out of the proliferated news. There was a massive audience seeking the news of all the US election polls. So, the dishonest news media and people who were to gain political and economical gain from the election decided to put that news on the social media and websites. Moreover, in third world countries like in Bangladesh where there is a massive lack of sense in the general public to check whether the news that are presented are true or not, fake news can do a lot of harm. Fake news can hit up communal riots, impact political agendas, harm economic stability and what not. One recent example in Bangladesh is in 2019, when four people were killed during a hindu muslim clash in Bhola district over a hacked Facebook post which was later proven not to be true. So, for sure unreliable news can spread so easily and do a lot of harm to the community and people without very much effort.

Identifying fake news has become a major interest in Natural Language Processing(NLP) researchers. Not only because it has certain challenges and characteristics that an NLP researcher might find interesting, but also because it has the potential to affect society in a very positive way. Major research institutions and universities are looking for the perfect model for this problem. Moreover, in this era of Large Language Models, this research is sure to improve and enlarge a lot. However, finding the perfect dataset is a major challenge for this domain. Datasets collected from newspapers in a manual way is a tremendously rigid job. So, collecting the datasets by web scraping the online newspapers is a much easier way. In today’s society most of the news consumed by the audience is delivered to social media. So, finding news from social media like Facebook, Twitter, Threads, Reddit should also be an important thing to look out for. After finding the dataset, data cleaning and preprocessing is also a major part. Perfect indexing and tokenization is mandatory for gaining the best accuracy. After revising the model and accuracy, letting the general public to test it is also a crucial step. Our main goal is to deliver a reliable and unreliable news classification system to be able to help the general public distinguish between the trustworthy and untrustworthy news that they regularly consume from different media and prevent any possible disturbance to the society caused by the unreliable news.

 

 

 

 

 

## **1.1 Motivation**

 

There is a fear of misinformation spreading in our society these days. Due to this wrong news, various situations in different countries such as conflicts between political parties, loss of people, wrong power of people are coming out day by day. To deal with this kind of situation, we must work especially to detect this fake news. Due to this wrong news, we can't trust any kind of social media such as Twitter, Facebook, YouTube etc. False information can generate massive cultural damage, panic, and conflict activity. The spread of false information between different communities can increase conflict and create tension in the social whole. Accurate news is important during disasters, natural disasters or emergencies. Credible information on public safety helps people take necessary action. Unreliable news can create panic in public epidemics and reduce the ability to take timely action. Credible news advises people on cultural, political and economic issues, so that they can participate in that assessment. The US presidential election campaign is also affected by the unreliable news. So, it is a major cybersecurity threat which is very alarming in today's modern world.  It is a big problem for the public to know which is reliable and accurate information and at the same time which is unreliable and inaccurate information. Unreliable information should be detected and also sharing of this unreliable information should be stopped as soon as possible before it causes any makassar situation in any country. So we develop an intelligent system which can easily detect which news or information is reliable and which is not .

 

 

 

 

 

 

 

 

 

 

 

 

 

 

 

 

 

 

## **1.2 Business Model Canvas**

**Figure 1: Business Model Canvas**

 

 

 

 

 

 

 

 

## **1.3 Organization of this Capstone Report**

 In the background we talked about the problem and how we wanted to solve the problem. Later we talked about related works of various authors and their contributions in this area. Later we discussed the programme outcome and answered the necessary questions. After that we showed up with some research questions and they were answered. And finally, we talked about the data analysis plan and gave details about our planned methodology.

 

 

 

 

 

 

 

 

 

 

 

 

 

 

 

 

 

 

 

 

 

# **Chapter 2**

 

**Literature Review**

## **2.1 Background**

Generally, the newspapers want to publish the most reliable and trustworthy news in the papers. But nowadays there are a lot of ways to deliver news to the public. TV media, print media, online news portals, even social media are being used to distribute news. So, verifying the authentication has become a major challenge for the news portals. Usually, what the average news portals do is collect news from other sources and they try to publish it as quickly as they can to deliver it to the audience by verifying the sources minimally to fasten the delivery time. Moreover, big political agendas to unrest the society to gain economic benefit do play a vital role in the initiation of unreliable news \[1\]. Whether it is hard to detect fake news manually or there is a ton of unreliable news flow nowadays, detecting them automatically has become a crying need in this age.

Machine Learning and NLP based algorithms are the best way to go for detecting unreliable news. But for that, we need datasets of multiple news sources across the varieties of news types. Again, language is also an important thing to remember. If we are going to work on the Bengali news, then we will need Bengali datasets. But detecting Bengali speeches is also a challenging thing because of the complexity and deep meaning inside any speech in Bengali language \[2\].

 

## **2.2 Related Works**

A massive flow of fake news has been spread around the world in recent years because of the ease of technology access to a whole lot of people. Social media and online news portals have become a massive part of this disastrous culture. So, a massive growth of interest has been grown in the field of building intelligent systems to detect unreliable news automatically. The existing work needs improvement in measures of both datasets and accuracy. This article showed two new kinds of datasets where one is collected by crowdsourcing and another is from the web where they got celebrity fake news \[3\]. They did cross domain analyses and also matched their machine accuracy with actual human accuracy. This research group implemented a fake news detection system using Thai text datasets \[4\]. Their texts had to be segmented with advanced techniques like word wrapping, word matching etc. like English words because the Thai words are written in a continuous manner. Their dataset worked best on LSTM technique which is basically a deep learning model that gained a full accuracy on the test sets. This research uses a wide variety of data preprocessing techniques on the dataset \[5\]. They used normalisation, stop words removal, lemmatization on the news headlines, body and descriptions. They used Tf-idf to find and remove the most frequent stop words such as a, an, the, was etc. in the news descriptions. They designed the model not only with classification results from the content-based datasets, but also with social media features. Their proposed model combined the results from social media features that are based on content and resulted in a more accurate model which gives 90% accuracy This investigation cantered its attention on the identification of fabricated news articles disseminated through social media platforms by employing data mining methodologies \[6\]. The authors proposed a framework that combined linguistic analysis, source credibility analysis, and social context analysis to identify and classify fake news. They employed machine learning algorithms such as SVM, Naive Bayes, and Random Forest for classification and achieved promising results. This systematic review investigated the use of NLP techniques in fake news detection \[7\]. The authors analysed various approaches, including feature-based, content-based, and stance-based methods. They identified the challenges associated with each approach and highlighted the potential of NLP for improving the accuracy of fake news detection systems. In this work, the authors explored the application of deep learning techniques, specifically convolutional neural networks (CNN) and long short-term memory (LSTM) networks, for fake news detection \[8\]. They used word embeddings and textual features to train their models and achieved competitive performance compared to traditional machine learning algorithms. This review paper focused on the use of stance-based approaches for fake news detection \[9\]. Stance refers to the position or perspective expressed towards a particular topic in a news article. The authors discussed different methods, such as lexical and semantic-based approaches, that leverage stance information to classify news articles as fake or real. In this study, the authors proposed a deep diffusive neural network (D2NN) for fake news detection \[10\]. The D2NN model incorporated both textual and social network information to capture the propagation patterns of fake news. Experimental results showed the effectiveness of their approach in identifying fake news stories. This study proposed a fake news detection framework specifically designed for Bangla language content \[11\]. The authors employed machine learning and deep learning techniques, including Support Vector Machines (SVM), Multilayer Perceptron (MLP), and LSTM networks, to classify news articles as fake or real. The experimental results demonstrated the effectiveness of the proposed approach. This research focused on detecting misinformation in Bangla news by combining stance analysis and ensemble learning techniques \[12\]. The authors extracted linguistic features and utilised an ensemble of classifiers, including Naive Bayes, Decision Tree, and Random Forest, to identify fake news articles. The proposed approach showed promising results in Bangla fake news detection. In this study they proposed a unreliable news detection framework for Hindi language using machine learning and text mining techniques \[13\]. The authors employed feature extraction, including Bag-of-Words and TF-IDF, and utilised algorithms such as Random Forest and Naive Bayes for classification. The experimental results demonstrated the effectiveness of the proposed approach in detecting fake news in Hindi. The paper best suggests the LSTM algorithm with 92% for fake news classification. The paper proposes a recurrent neural network (RNN) model for sentence-level text classification \[14\]. It explores the use of multi-task learning to jointly learn sentence representations for multiple classification tasks and shows improved performance compared to single-task learning.

 

 

 

 

 

 

 

 

 

# **Chapter 3**

 

                     	      	**Addressing the Program Outcomes (PO’s)**

Our Capstone Project's primary research question is provided below.

**RQ1.** How to apply and integrate new and previously learned mathematics, physics, and engineering knowledge to handle the Capstone Project's challenges? (PO1)

⮚ Our Capstone Project needs to be dealt with machine learning and deep learning. We know that those domains of Computers Science consist of a lot of maths, physics, and engineering knowledge. We have previously learnt them and are continuously learning to implement them to improve our project.

 

 

**RQ2.** What relevant topics needed to be investigated, and how should the Capstone Project's issues and objectives be defined? (PO4)

To address the objective of building an intelligent system that detects fake news through machine learning, a comprehensive investigation of relevant topics is necessary. This involves exploring existing literature and gathering related papers that pertain to the topic. By analysing these sources, we can identify the types of implementations that have been previously utilised and the models that have been employed. Additionally, we aim to understand the limitations and challenges encountered in previous implementations. The goal of our capstone project is to provide improved solutions or overcome these limitations by focusing on delivering better detection mechanisms. Through this process, we can define the issues and objectives of our project more effectively, enabling us to contribute to the advancement of unreliable news detection using machine learning techniques.

 

 

**RQ3.** How do you assess the numerous components of the Capstone Project objectives in order to build an efficient solution? (PO2)

To build an efficient solution for the Capstone Project, the components of the objectives are assessed by comparing the system's summarization with human-created summaries. ROUGE is used to evaluate the performance, ensuring accurate and effective information summarization.

**RQ4.** How to design and build capstone project solutions that address public health and safety, cultural, societal, and environmental concerns? (PO3)

To address public health and safety, cultural, societal, and environmental concerns in the design and development of the capstone project, several measures are taken. While the dataset primarily contains classified real or fake news articles, steps are implemented to avoid harm and misinformation dissemination. Cultural and societal sensitivities are respected by considering language, tone, and content alignment with cultural values. Bias avoidance and inclusivity are emphasised during the classification process. Although the project's primary focus may not be environmental, sustainable practices are followed to minimise resource consumption and waste generation. By incorporating these considerations, the capstone project ensures ethical conduct, societal responsibility, and sustainable approaches for a well-rounded solution.

**RQ5.** Which modern engineering and IT technologies are necessary, and how should they be used to design and develop the Capstone Project solution? (PO5)

⮚  The production of high-quality items requires both efficient design and manufacturing, which are closely connected. For this modern engineering and IT technologies are really significant.

 ⮚   For our system we are planning to use high-level programming language python and python libraries like pandas, numPy etc.

 ⮚   We will try to design our system with the help of modern Machine Learning tools like TensorFlow, Scikit-learn, PyTorch etc.

 ⮚   We are planning to prepare the best dataset. We will scrape our dataset from the web by scraping tools like BoilerPy.

 

 

**RQ6.** How should the societal, health, safety, legal, and cultural components of the capstone project be evaluated and resolved? (PO6)

Our business model ensures that there are no negative impacts on societal, health, safety, legal, and cultural aspects. To assess the efficiency of our system, we will gather feedback through surveys. Human evaluation will provide a more accurate and comprehensive understanding of the system's performance and effectiveness.

 

 

**RQ7.** How to assess and deliver sustainability impact of the capstone project in societal and environmental perspectives? (PO7)

Our objective is to develop a highly efficient model that minimises CPU processing power, resulting in lower energy consumption over time. This approach contributes to environmental sustainability. Additionally, we ensure that our collected data adheres to legal requirements, and all user information is securely stored in encrypted format, mitigating any potential privacy concerns.

 

**RQ8.** Which professional and engineering professional standards and practices should be observed during the capstone project's implementation? (PO8)

Our objective is to develop a highly efficient model that minimises CPU processing power, resulting in lower energy consumption over time. This approach contributes to environmental sustainability. Additionally, we ensure that our collected data adheres to legal requirements, and all user information is securely stored in encrypted format, mitigating any potential privacy concerns.

 

**RQ9.** Which practices should be followed in order to function well as an individual and a team member in order to achieve the Capstone Project's objectives? (PO9)

Our capstone project is a collaborative effort, where we work as a team. We gather data, design, and develop the project together. Communication with various stakeholders is also crucial. In order to establish a standardised dataset, it is important to involve a human evaluator. Their expertise will help ensure relevance, minimise redundancy, and maintain coherency. Additionally, we will engage literature experts for their input. Regular communication with clients is essential throughout the project

 

**RQ10.** Which procedures should be followed in order to provide optimal deliverables? (PO10)

To provide optimal deliverables, the capstone project will employ Agile procedures, following every stage of software engineering. Emphasis will be placed on generating updated summaries and ensuring quality through rigorous testing and adherence to structured development processes.

 

**RQ11.** How to apply software engineering concepts and techniques to the Capstone Project's development life cycle and conduct economic analysis and cost estimation in the event of a real-world deployment of the Capstone Project's solution? (PO11)

The Agile life cycle in software engineering enables economic analysis and cost estimation in real-world deployment, facilitating the optimization of profit margins. By employing Agile practices, the project aims to enhance economic efficiency and effectively manage cost considerations in order to maximise profitability.

 

 

**RQ12.** Which independent and life-long learning skills are gained during the Capstone Project design and development process? (PO12)

Through the Capstone Project design and development process, students gain valuable independent and life-long learning skills. They acquire practical problem-solving abilities by addressing real-life challenges, implementing frameworks in large-scale projects, and utilising databases effectively. Moreover, they learn to apply previously acquired knowledge and intelligence in new contexts, fostering adaptability and innovation. This experience enhances their overall understanding and experience in solving real-world problems, implementing frameworks, leveraging databases, and leveraging prior knowledge effectively. These skills contribute to their personal and professional growth, preparing them for future endeavours and enabling them to tackle complex challenges with confidence.

 

**The present phase of the Capstone Project is primarily concerned with the first two research questions, particularly RQ1 and RQ4.**

 

 

 

 

 

 

 

 

 

 

 

 

 

# **Chapter 4**

 

**Research Questions & Objectives**

**Research Questions:**

●       How will this system detect the unreliable news?  
●       How will this system handle different models for different languages?  
●       How will this system find the result from the different types of models?

●       How effective are the various types of ML/DL algorithms to detect unreliable news?

 The main goal of this research is to create a system that can effectively detect and classify unreliable news from online news websites. For this our system uses various models of machine learning and deep learning. Then the system produces accurate results by integrating the results of these models. Another goal is to compare the proposed model with existing models and demonstrate its effectiveness in detecting and classifying unreliable news. This system selects two types of users: Content creators and readers. Allowing content creators to certify the authenticity of news articles and posts can do a lot in curbing the spread of unreliable information. In addition, the research paper aims to create a system that can give users warning signals about the presence of unreliable news in their content. While most of the existing research focuses on single-language analysis, this work aims to address the challenge of unreliable news recognition in multiple languages. Additionally, this research paper aims to develop an API system that facilitates the detection and classification of unreliable news. The proposed system uses various models for machine learning, deep learning and natural language processing. Finally, this study aims to demonstrate the effectiveness of these models in identifying and addressing unreliable news.

 

 

 

 

 

 

# **Chapter 5**

 

      	                       	                                                      **System Design**

## **5.1   Use Case Diagram**

Here are three actors. Admin, user and system. Admin can use the website, view the users, and update the info of users. Users can use the website and check the reliability of any kind of news. The system can predict the reliability of any news.

 

**Figure 2: Use Case Diagram**

## **5.2    Activity Diagram (Swimlane)**

Three actors admin, user, and system have different tasks. After starting the processing, the first users will enter the system. Then admin will record their information and maintain the features. Then the system will show the control features and user information. Then users will check the news. We can make a decision here.  If the news is a real news, the system will process and predict the news and processing will end but if the news is fake the system will not work and instantly the processing will be finished.

**Figure 3:** **Activity Diagram (Swimlane).**

 

##  **5.3  Class Diagram**

Here are 4 classes. Users, admin, a system that means an admin can control one system. The attributes of admin are adminID name and address and methods are updating view and maintain. The attributes of users are id name and date of birth. Methods are viewing the website and uploading news. The attributes of systems are name and code, and methods are classifying news and show info. The attributes of news classification are news, and methods are taking input, processing news, and predicting result. User can register in website and view the reliable and unreliable news. 

 

 

**Figure 4: Class Diagram.**

 

 

 

 

 

 

## **5.4   Component Diagram**

Here are 4 components. Users, admin, system and upload news. There are some provided and required relationships between these components. Like admin can provide system features to users and users require it from admin to use the system. Admin wants to show the users info from the system and system can provide the users info to admin. Users need information and system will give information to users. Uploaded any kind of news and the system will verify the reliability of the news.

 

 

**Figure 5:** **Component Diagram.**

 

 

 

##  

## **5.5   Sequence Diagram**

Here are 3 actors. Admin, users and system. Here are some activation methods. When admin and system are active, admin can provide features to system. Then users will be activated, and users can enter the system and want to see features then system will show them the features. Then admin will be again activated and fetch users' info from system and save it into the system. The users will upload the news in system and system will tell them about the verification of a news.

 

 

**Figure 6:** **Sequence Diagram.**

 

 

 

## **5.6   Data Flow Diagram**

Here are 3 processes and 3 entities. And they have some data flow. Processes are fake news, news and information. Entities are users, system and admin. Users will upload the news and system will process the news. System will predict the reliable and unreliable news and it will be shown to users. Uploaded news will be stored to a database. Admin will manage the information of users and the information of users will be saved into another database called user info.

 

 

 

**Figure 7:** **Data Flow Diagram.**

 

 

 

 

 

 

 

 

## **5.7   Deployment Diagram**

There is a web server with Django connected with another web server in tcp/ip connection. In another web server there are two components' users and news option. This web server relates to a database server which has two components admin and database in tcp/ip connection. There is another web server which component is browser and there is a firewall between web server database server and end user client. They are connected through a firewall.

 

 

 

**Figure 8:** **Deployment Diagram.**

 

 

##  **5.8  User Interface**

 

This is the user interface of our website where the users can upload news and check the reliability or unreliability of that news.

 

 

**Figure 9: User Interface**

 

 

 

 

 

 

 

 

# **Chapter 6**

 

**Materials and Method**

## **6.1 Materials:**

### **6.1.1 Data Collection:**

 

The data was collected from IEEE Explore (WelFake dataset.)  It  is a dataset of 72,134 news articles with 35,028 real and 37,106 fake news. It was published in IEEE Transactions on Computational Social Systems ( Volume: 8, Issue: 4, August 2021). The authors merged four popular news datasets (i.e. Kaggle, McIntire, Reuters, BuzzFeed Political) to prevent over-fitting of classifiers and to provide more text data for better ML training.

 

### **6.1.2 Dataset Exploration:**

 

Dataset contains four columns:

1\.  	Serial number (starting from 0);

2\.  	Title (about the text news heading);

3\.  	Text (about the news content); and

4\.  	Label (0 \= fake and 1 \= real).

 

There are 78098 data entries in csv file out of which only 72134 entries are accessed as per the data frame. The dataset was cleaned by dropping the rows with null values.

 

###  

### **6.1.3 Dataset Cleaning:**

 

The practice of correcting or eliminating inaccurate, corrupted, improperly formatted, duplicate, or incomplete data from a dataset is known as data cleaning. There are numerous ways for data to be duplicated or incorrectly categorized when integrating different data sources. Even though results and algorithms seem correct, they are unreliable if the data is inaccurate. Because the procedures will differ depending on the dataset, there is no one set method that can be used to prescribe the precise steps in the data cleaning process. For doing this project, we need to install natural language toolkit (nltk). This module is useful for cleaning data. For example, removing stopwords or useless words (person, preposition, article, be verb etc.), convert words to its base form etc. We also use TfidfVectorizer which is help us to split sentence into token.

 

## **6.2 Method:**

 

### **6.2.1 Proposed Model:**

**SVM:**

 

Input Layer:

 

Nodes representing input features.

Weighted Sum Layer:

 

Compute the weighted sum of inputs (sum of products of input values and their corresponding weights).

Margin Layer:

 

Evaluate the distance of the data point from the decision boundary. SVM aims to maximize this margin.

Kernel Layer (optional):

 

Apply a kernel function to transform the input features into a higher-dimensional space (optional, depending on the kernelized SVM variant).

Output Layer:

 

Final output representing the predicted class, determined by the side of the decision boundary the data point falls on.

The SVM architecture is somewhat similar to logistic regression but has a focus on maximizing the margin between different classes, making it a powerful algorithm for both linear and non-linear classification problems.

 

### **6.2.2 Design/Framework:**

**Logistic Regression:**

Input Layer:

Nodes representing input features.

Weighted Sum Layer:

Compute the weighted sum of inputs (sum of products of input values and their corresponding weights).

Activation Layer:

Apply the sigmoid activation function to the weighted sum to squash the output between 0 and 1\.

Output Layer:

Final output representing the predicted probability of belonging to the positive class.

That's a high-level view of the layers in a logistic regression model, emphasizing the flow of information from input to output. Input Layer:

Nodes representing input features.

Weighted Sum Layer:

Compute the weighted sum of inputs (sum of products of input values and their corresponding weights).

Activation Layer:

Apply the sigmoid activation function to the weighted sum to squash the output between 0 and 1\.

Output Layer:

Final output representing the predicted probability of belonging to the positive class.

That's a high-level view of the layers in a logistic regression model, emphasizing the flow of information from input to output.

 

 

 

### **6.2.3 Algorithm/ Model Formulation**

**BEGIN**

1\. Input: Dataset

2\. Output: Probability between 2 classes.

3\. Initialize: lr(0.0001), epochs(30), C (regularization parameter)

4\. X\_train, X\_test \= train\_test\_split(dataset)

5\. While epoch \<= epochs:

   a. Forward pass:

  	i. SVM loss calculation:

     	\- Compute margin for each training example

     	\- Compute hinge loss

  	ii. Backward pass (Compute gradients and update weights using optimization algorithm like SGD)

     	\- Update weights using gradients and regularization term

   b. Evaluate on validation set if needed

   c. epoch \= epoch \+ 1

6\. End While

**END**

 

 

# **Chapter 7**

 

**Results and Discussions**

## **7.1 Obtained Results**

 

 

Using different Machine learning and Natural Language processing model for different languages,

we got better results for SVM and Logistic Regression.

**Figure 6.1.1: Confusion Matrix for Logistic Regression**

 

 

 

 

 

 

 

**Figure 6.1.2: Confusion Matrix for SVM**

 

 

 

 

 

 

 

 

 

 

 

## **7.2 In depth Result Analysis**

 

| ML Models | Accuracy Score | Precision Score | Recall Score | F1- Score |
| :---: | :---: | :---: | :---: | :---: |
| Logistic Regression | 0.94243706 | 0.94260419 | 0.94243706 | 0.94241001 |
| Support Vector Machine | 0.95852694 | 0.95863177 | 0.95852694 | 0.95851282 |

 

 

Precision, Recall, and F1-Score are some fundamental concepts. These must deal with gaining a more detailed understanding of a classifier's performance as opposed to focusing only on overall accuracy.

 

**Accuracy:** The primary metric utilized for model evaluation is commonly Accuracy, which indicates the proportion of accurate predictions among all predictions:

 

Accuracy Formulas in the above.

 

This is arguably the most straightforward model evaluation metric and is frequently employed. However, it's beneficial to delve a bit further.

 

**Precision**: Precision indicates the accuracy of positive predictions (true positives). The formula for this metric is:

 

 

All three descriptions above essentially convey the same concept, with the last one providing a specific example using cancer as a case.

 

 

**Recall / Sensitivity:** The percentage of positive cases that the classifier accurately predicted out of all the positive cases in the data is called recall. Another name for it is sensitivity at times. The following is the formula for it:

 

 

**F1- Score:** Once again, these are just different expressions of the same formula. In the instance of the cancer example and using data from the confusion matrix, recall would be: F1 score is a measure that combines precision and recall. It is commonly described as **a** harmonious medium of the two. Harmonic mean is another method of calculating the "average" of values ​​and is generally said to be better suited for ratios (such as precision and recall) than traditional arithmetic mean.

The formula used for the F1 score in this case is:

 

 

 

The idea is to provide a single metric that weights the two ratios (precision and recall) in a balanced way, requiring both to have a higher value for the F1-score value to rise. This is because the F1-score is more impressible to one of the two inputs having a lowest value (0.01 here). Which makes it massive if you want to poise the two.

## **7.3 Software Cost Analysis**

To estimate the cost impact of classifying the real and fake news  , we need to consider the following factors:

 

▪ Project size: The project size will affect the cost of development, as well as the cost of maintenance and support.

 

▪ Project complexity: The complexity of the project is rated on a scale of 1 to 5, with 5 being the most complex. The complexity of the project will affect the cost of development, as well as the cost of maintenance and support.

 

▪ Development team experience: The experience of the development team will also affect the cost of developing and maintaining the system. A more experienced team will be able to develop the system more quickly and efficiently, which will reduce the cost. The cost impact of fake news detection can be significant. By automating the detection process, the cost of manual labor can be reduced. Additionally, the automated system can be used to detect caries more quickly and accurately than human inspectors.

 

 Here is a cost analysis calculation for detecting fake news web app-

(we follow the salary structure of Amber IT a software development company)

We assume that we need 5 months for the project and 4 employees work in the project.

 

Junior Developer (x2)   : 30,000 per month

Front End Designer (x2): 35,000 per month

 

Total Monthly Salary Cost:

 

2×1,0000 \+ 2×10000 \= 40000

 

Total Salary Cost for 5 months:

 

5×40,000 \= 2, 00,000

 

            Other Costs:

●   	Look for more cost-effective software/tools licenses.

●   	Explore affordable hosting solutions.

Revised Other Costs:

●   	Software/Tools Licenses: 20,000

●   	Server Hosting: 80,000

●   	Miscellaneous Expenses: 10,000

 

Total Other Costs:

 

20,000+80,000+15,000= 1,15,000

 

 

 

Total Estimated Budget :

2, 00,000 \+ 1,15,000 \= 3,15,000

 

 

 

 

 

 

 

**Cocomo 2**

 To estimate the cost impact of using COCOMO to automate fake news detection, we will follow the approach with the following factors:

 

            Project Size (ESLOC): Estimate the size of the project in terms of Estimated Source Lines of Code (ESLOC). This will depend on the complexity and features of the caries detection system.

             

            Project Complexity: Rate the complexity of the project on a scale of 1 to 5, with 5 being the most complex. The complexity rating will affect both development and maintenance costs.

             

            Development Team Experience: Assess the experience of the development team. A more experienced team can develop and maintain the system more efficiently, potentially reducing costs.

 

 

Here is a cost analysis calculation for automating dental caries detection using COCOMO:

 

Project size: 10,000 ESLOC

 

Project complexity: 2

 

Development team experience: Experienced

 

COCOMO cost estimation model:

 

The COCOMO cost estimation model is a mathematical model that can be used to estimate the cost of developing a software project. The model considers the size and complexity of the project, as well as the experience of the development team. The following formula can be used to calculate the cost of a software project using COCOMO:

             

Cost \= K \* E \* (ESLOC) ^ F

 

where:

 

▪ K is a constant that depends on the development team experience

▪ E is a constant that depends on the project complexity

▪ ESLOC is the estimated source lines of code

▪ F is a constant that depends on the project complexity

 

 

 

 

Sample calculation:

 

Using the following values:

 

 ▪ K \= 2

 ▪ E \= 2

 ▪ ESLOC \= 5000

 ▪ F \= 1.12

 

The cost of the project can be calculated as follows:

 

 Cost \= 2 \* 2 \* (5000) ^ 1.12  \=  22400 ≈ 22500

 

 

We assume that we need 5 months for the project and 4 employees work in the project.

 

Junior Developer (x2): 30,000 per month

Front End Designer (x2): 15,000 per month

 

Total Monthly Salary Cost:

 

2×30,000 \+ 2×15,000 \= 90000

 

Total Salary Cost for 5 months:

 

5×90,000 \= 4,50,000

 

            Other Costs:

●   	Look for more cost-effective software/tools licenses.

●   	Explore affordable hosting solutions.

Revised Other Costs:

●   	Software/Tools Licenses: 30,000

●   	Server Hosting: 80,000

●   	Miscellaneous Expenses: 15,000

 

Total Other Costs:

 

30,000+80,000+15,000= 1,25,000

 

 

 

Total Estimated Budget :

4,50,000 \+ 1,25,000 \= 5,75,000

 

 

 

 

# **Chapter 8**

 

**Conclusion**

 

We believe that our machine learning model for identifying trustworthy and untrustworthy news has great promise to enable people to make sense of the ever-complex world of information. The methodology assures dependable identification of legitimate and unreliable news sources with an amazing accuracy of 94.24%. This competency promotes media literacy and critical thinking by assisting users in making well-informed judgments about the content they consume. Our methodology helps combat disinformation by differentiating between reliable and questionable news, fostering a more knowledgeable and astute audience. Our product is a useful tool that helps people obtain correct and trustworthy information in a time when unreliable news is a sober impendence for the community. This strengthens the basis of an informed and democratic society.

 

 

 

 

 

 

 

 

 

 

 

 

# **References**

 

1. Traylor, T., Straub, J., Gurmeet, & Snell, N. (2019). Classifying Fake News Articles Using Natural Language Processing to Identify In-Article Attribution as a Supervised Learning Estimator. *2019 IEEE 13th International Conference on Semantic Computing (ICSC)*, 445–449. https://doi.org/10.1109/ICOSC.2019.8665593  
2. Islam, J., Mubassira, M., Islam, Md. R., & Das, A. K. (2019). A Speech Recognition System for Bengali Language using Recurrent Neural Network. *2019 IEEE 4th International Conference on Computer and Communication Systems (ICCCS)*, 73–76. https://doi.org/10.1109/CCOMS.2019.8821629  
3. Pérez-Rosas, V., Kleinberg, B., Lefevre, A., & Mihalcea, R. (n.d.). *Automatic Detection of Fake News*. http://wiki.dbpedia.org/about  
4. Meesad, P. (2021). Thai Fake News Detection Based on Information Retrieval, Natural Language Processing and Machine Learning. *SN Computer Science*, *2*(6), 425\. https://doi.org/10.1007/s42979-021-00775-6  
5. Bauskar, S., Badole, V., Jain, P., & Chawla, M. (2019). Natural Language Processing based Hybrid Model for Detecting Fake News Using Content-Based Features and Social Features. *International Journal of Information Engineering and Electronic Business*, *11*(4), 1–10. https://doi.org/10.5815/ijieeb.2019.04.01  
6. Shu, K., Sliva, A., Wang, S., Tang, J., & Liu, H. (2017). Fake News Detection on Social Media. *ACM SIGKDD Explorations Newsletter*, *19*(1), 22–36. https://doi.org/10.1145/3137597.3137600  
7. Karimi, H., Roy, C., Saba-Sadiya, S., & Tang, J. (n.d.). *Multi-Source Multi-Class Fake News Detection*.  
8. Ruchansky, N., Seo, S., & Liu, Y. (2017). CSI. *Proceedings of the 2017 ACM on Conference on Information and Knowledge Management*, 797–806. https://doi.org/10.1145/3132847.3132877  
9. Lillie, A. E., & Middelboe, E. R. (2019). *Fake News Detection using Stance Classification: A Survey*. [http://arxiv.org/abs/1907.00181](http://arxiv.org/abs/1907.00181)  
10. Zhang, J., Dong, B., & Yu, P. S. (2019). Deep Diffusive Neural Network based Fake News Detection from Heterogeneous Social Networks. *2019 IEEE International Conference on Big Data (Big Data)*, 1259–1266. https://doi.org/10.1109/BigData47090.2019.9005556  
11. Hossain, E., Nadim Kaysar, Md., Jalal Uddin Joy, A. Z. Md., Mizanur Rahman, Md., & Wahidur Rahman. (2022). *A Study Towards Bangla Fake News Detection Using Machine Learning and Deep Learning* (pp. 79–95). https://doi.org/10.1007/978-981-16-5157-1\_7  
12. Tohabar, Md. Y., Nasrah, N., & Samir, A. M. (2021). Bengali Fake News Detection Using Machine Learning and Effectiveness of Sentiment as a Feature. *2021 Joint 10th International Conference on Informatics, Electronics & Vision (ICIEV) and 2021 5th International Conference on Imaging, Vision & Pattern Recognition (IcIVPR)*, 1–8. https://doi.org/10.1109/ICIEVicIVPR52578.2021.9564138  
13. Kumar, S., & Singh, T. D. (2022). Fake news detection on Hindi news dataset. *Global Transitions Proceedings*, *3*(1), 289–297. https://doi.org/10.1016/j.gltp.2022.03.014  
14. Liu, P., Qiu, X., & Huang, X. (n.d.). *Recurrent Neural Network for Text Classification with Multi-Task Learning*. Retrieved July 5, 2023, from [https://arxiv.org/abs/1605.05101](https://arxiv.org/abs/1605.05101)

 

 

 

 

# **Appendix**

 

 

| CO | Details | Knowledge Profile (K) | Engineering Problem (EP) |
| :---- | :---- | :---- | :---- |
| CO3 | In various digital platforms, the presence of unreliable news is prevalent in textual formats like articles, headlines, social media posts, comments, and online forums. We have developed a model proficient in categorizing textual data into two classes: • Reliable News • Unreliable News Contemporary attention is directed towards computational tools, including Machine Learning, Deep Learning, and Natural Language Processing (NLP), to effectively identify and differentiate between reliable and unreliable news. Our goal is to predict the credibility of news articles using NLP, machine learning, and deep learning algorithms. The aim is to identify the optimal predictive model and construct a reliable news detector based on this model.   | **(i) Problem Analysis \[K1, K2, K3, K4\]   K1: Theory-based natural Sciences:** Gained Knowledge about unreliable news detection and studied widely about it to identify the problems.   **  K2:        	Conceptually  	              	based mathematics,               	numerical analysis, statistics, and	formal aspects               	of   	computer                 	and information                 	science:** Statistical analysis and Numerical analysis have been used.   **  K3: Theory-based engineering fundamentals:**        	 Programingg language, Deep Learning and Machine Learning NLP is being used to build the project.   **  K4: Forefront engineering specialist knowledge for practice:** With the help of machine learning and deep learning our approach has a substantial depth of knowledge to support social media user practices. | (**i) Problem Analysis \[EP1, EP2, EP3, EP6, EP7\]   EP1: Depth of knowledge required:** Need a clear understanding about text classification and its factors, and how can early detection and make collision free social media platform. Text classification type research papers to acquire knowledge about their objectives and processes.   **  EP2: Range of conflicting requirements:** We include an extended engineering, conflicting technical and other challenges such different types of models and a data augmentation approaches in our system. **EP3: Depth of analysis required:** To identify probable solution, we have gone through many machine learning and deep learning classifiers which have performed well in previous and also some classifiers that have not been used much to find out the best accuracy score. We needed abstract thinking and analysis to come up with the appropriate models for our project and track down the obvious solution. So, depth analysis is being performed to build an |

|   |   |   | efficient model to solve this problem.   EP6: Extent of stakeholder involvement and conflicting requirements: The point of view of stakeholders for this project is considered.   EP7: Interdependence: We have worked on high-level problems with multi lingual to solve this problem. |
| :---- | :---- | :---- | :---- |
| CO4 | Our project is based on programming. Our project goal is to predict unreliable and reliable news detection. We have not used any harmful components for public health in the project. The cultural and societal issues are considered in our project. The processes do not affect the culture and society negatively. Also, there will be no harmful impact on the environment because our project is software based where we will use computer tools. No harmful materials will be used to build the project. So, our project meets the environmental considerations. | **(i) Design and Implementation \[K5\] K5: Engineering design:** Engineering design is one of the vital parts of our project. In this design and implementation part, we have pointed out the problems that can emerge. Then, we also designed a prototype that can solve the problem. We explored a variety of machine learning and deep learning algorithms and designed our project in such a way that will be helpful for the social user people. | **(i)         	Design         	and Implementation	\[EP1,              	EP2, EP4, EP5, EP6, EP7\] EP1: Depth of knowledge required:** Need a clear understanding about text classification and its factors, and how can early detection and make collision free social media platform. Text classification type research papers to acquire knowledge about their objectives and processes.   **EP2: Range of conflicting requirements:** We used various machine learning and deep learning models and also done pre-processing which were conflicting over our engineering design.   **EP4: Familiarity of issues:** There were conflict between Team members regarding the project design which were we solved. |

|   |   |   | EP5: Extent of applicable codes: We made our code understandable and maintain the code standard so that outside issues are covered by professional                              	engineering standards.   EP7: Interdependence: We have worked on high-level problems with multilingual to solve this problem. |
| :---- | :---- | :---- | :---- |
| CO5 | In our project, we have used python which includes lots of libraries. Python can be considered as a modern tool because it is broadly used for machine learning and deep learning. We also used some other modern tools such as logistic regression SVM, pandas etc. We used Google colab and VS code to build our models. | **(i) Materials and Devices \[K6\]   K6: Engineering Practice (technology):** Knowledge about machine learning and deep learning has been implemented using python**.** | **(i) Materials and Devices \[EP1, EP2, EP4, EP5\]     EP1: Depth of knowledge required:** Knowledge about the technical tools like logistic regression, SVM, Pandas, pre- processing techniques is used to  build this  project.   **  EP2:	Range	of         	conflicting requirements:**          	While implementing, 	we   faced uncommon issues in obtaining proper result. We have worked on this problem and have been able to solve it.   **EP4: Familiarity of issues:** Sometimes execution of models makes the computer slow and takes much time.  But for some |

|   |   |   | models Google Colab performs better.   EP5: Extent of applicable codes: Modern tools have been used to develop this project. We used standard design process and ethical problem-solving analysis while developing this project. |
| :---- | :---- | :---- | :---- |
| CO6 | Our capstone project has no such harmful societal and environmental impact. This project can help all the mankind. There is no gender discrimination or limitation of age group. This is a web \- based application. So, there are no harmful elements or materials are used. Our project only requires computer and some software.. Our project will help people in the social platform sectors to detect the problem earlier. All the people from different religious group can use our system. So, there are no cultural and legal issues. | **(i) Social and Environmental Impact of Engineering \[K7\]   K7: Comprehension of engineering in society:** There is no presence of any harmful activities socially, economically, and culturally. There is no illegal software is used in our project. There are no negative consequences for social and environmental engineering. We provide trustworthy and valid references in this project. | **(i) Social and Environmental Impact of Engineering \[EP2, EP5, EP6\]   EP2: Range of conflicting requirements:** Tools, we have used to develop the project has been selected considering social, environmental, and cultural aspects.   **EP5: Extent of applicable codes:** Modern tools have been used to develop this project. We used standard design process and ethical problem-solving analysis while developing this project.   **EP6: Extent of                     	stakeholder involvement	and                       	conflicting requirements:**       	                     	The Stakeholder might change their perspective or needs considering societal, cultural issues. |

 

 

