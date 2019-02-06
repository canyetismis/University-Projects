#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include "coursework.h"
#include <semaphore.h>

int totalResp = 0;
int totalTurn = 0;
int pCount = 0;
int destroyP = 0;

pthread_mutex_t mutex;
pthread_mutex_t valMutex;
pthread_mutex_t printMutex;  // Controls access to critical section
sem_t empty;    		// counts number of empty buffer slots
sem_t full;             // counts number of full buffer slots

struct process *head = NULL;
struct process *tail = NULL;
/*struct process *printList(struct process *p){
	struct process *temp = p;
	int i = 0;
	while(temp != NULL) {
		i++;
		temp = temp->oNext;
	}
	printf("Number of things: %d\n", i);
	return p;
}*/

void addToList(struct process **head, struct process * new, struct process **tail){
	if(*head == NULL){
		*head = new;
		*tail = new;
	} else{
		(*tail)->oNext = new;
		*tail = new;
		
	}	
}

/*void printList(struct process *p){
	struct process *temp = p;
	int test = 0;
	if(temp == NULL){
		printf("cunt\n");
	}
	while(temp != NULL){
		temp = temp->oNext;
		test += 1;
	}
	printf("%d\n", test);
	
}*/

void *funcProducer(){
	int i = 0;
	for(i = 0; i< NUMBER_OF_PROCESSES; i++){  
		                
		struct process *newProcess = NULL;
		newProcess = generateProcess();
		sem_wait(&empty);
		pthread_mutex_lock(&mutex);
		
        addToList(&head, newProcess, &tail);
        //printList(head);
		
		pthread_mutex_unlock(&mutex);
		sem_post(&full);

	}
	//sem_post(&full);
	pthread_exit(NULL);
}
struct process *removeHead(struct process **head){
	struct process *temp = *head;
	*head = (*head)->oNext;
	temp->oNext = NULL;
	return temp;
}
void *funcConsumer(void * id){
	//printf("created thread\n");
	struct timeval start;
	struct timeval end;
	int CID = 0;
	
	CID = *((int *)id);
	
	
    while(1){
		sem_wait(&full);
		pthread_mutex_lock(&mutex);
		if(head == NULL){
			sem_post(&full);
			pthread_mutex_unlock(&mutex);
			pthread_exit(NULL);
		}

		struct process * p = removeHead(&head);
		//p->oNext = NULL;
        //printList(temp);

		pthread_mutex_unlock(&mutex);
        
        pthread_mutex_lock(&printMutex);
        int resp = 0;
		int turn = 0;
		
		int initial_burst_time = p->iBurstTime;
		int check = p->iState;
		simulateRoundRobinProcess(p, &start, &end);
		turn = getDifferenceInMilliSeconds(p->oTimeCreated, end);
		resp = getDifferenceInMilliSeconds(p->oTimeCreated, start);
		
		printf("CONSUMER ID = %d, Process ID = %d, Previous Burst Time = %d, New Burst Time = %d",
            CID,
            p->iProcessId, 
			initial_burst_time, 
			p->iBurstTime
		);
		if(check == NEW){
            printf(", Response Time = %d", resp);
            pthread_mutex_lock(&valMutex);
            totalResp += resp;
            pthread_mutex_unlock(&valMutex);
		}
		if(p->iState == FINISHED){
			printf(", Turn Around Time = %d ", turn);
            pthread_mutex_lock(&valMutex);
            totalTurn += turn;
			pthread_mutex_unlock(&valMutex);
			sem_post(&empty);
			free(p);
		}else {
			//printList(head);
			sem_post(&full);
			pthread_mutex_lock(&mutex);			
			addToList(&head, p, &tail);
            pthread_mutex_unlock(&mutex);
		}
		printf("\n");
		pthread_mutex_unlock(&printMutex);	
		
		pthread_mutex_lock(&mutex);
		if(head == NULL){
			sem_post(&full);
			pthread_mutex_unlock(&mutex);
			pthread_exit(NULL);
		}
		pthread_mutex_unlock(&mutex);
		

	}
	
}

int main(){
	int i = 0;
    int cid [NUMBER_OF_CONSUMERS];

	pthread_t producer;
	pthread_t consumer[NUMBER_OF_CONSUMERS];
	
	pthread_mutex_init(&mutex, NULL);
	pthread_mutex_init(&valMutex, NULL);
	pthread_mutex_init(&printMutex, NULL);
	sem_init(&empty,0,BUFFER_SIZE);
	sem_init(&full,0,0);
    
    pthread_create(&producer, NULL, funcProducer, NULL);
    for(i = 0; i<NUMBER_OF_CONSUMERS; i++){
		cid[i] = i + 1;
        if (pthread_create(&(consumer[i]), NULL, funcConsumer, (void *) &(cid[i])) == -1) {
			printf("Thread creation failed\n");
		}
    }
	
	sem_post(&full);

    pthread_join(producer, NULL);
    for(i = 0; i<NUMBER_OF_CONSUMERS;i++){
        pthread_join(consumer[i], NULL);
	}
	printf("The average Response Time is: %f\n", (double)totalResp / NUMBER_OF_PROCESSES);
	printf("The average Turn Around Time is: %f\n", (double)totalTurn / NUMBER_OF_PROCESSES);
    
}