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
pthread_mutex_t valMutex;  // Controls access to critical section
sem_t empty;    		// counts number of empty buffer slots
sem_t full;             // counts number of full buffer slots

struct process *head = NULL;

struct process * addToList(struct process * head, struct process * new){
	
	if(head == NULL || head->iBurstTime >= new->iBurstTime){
		new->oNext = head;
		head = new;
		return head;
	}
	else{
		struct process * curr = NULL;
		curr = head;
		while(curr->oNext != NULL && curr->oNext->iBurstTime < new->iBurstTime){
			curr = curr->oNext;			
		}
		new->oNext = curr->oNext;
		curr->oNext = new;
		return head;
	}
}

struct process *printList(struct process *p){
	struct process *temp = p;
	if(temp == NULL){
		printf("cunt\n");
	}
		printf("-----Process number %d here with burst time %d\n", temp->iProcessId, temp->iBurstTime);
	return p;
}

void funcProducer(){
	int i = 0;
	for(i = 0; i< NUMBER_OF_PROCESSES; i++){  
		                
		struct process *newProcess = NULL;
		newProcess = generateProcess();
		sem_wait(&empty);
		pthread_mutex_lock(&mutex);
		
		head = addToList(head, newProcess);
		
		pthread_mutex_unlock(&mutex);
		sem_post(&full);

	}
	//sem_post(&full);
}
struct process *removeHead(struct process **head){
	struct process *temp = *head;
	*head = (*head)->oNext;
	return temp;
}
void funcConsumer(void * id){
	//printf("created thread\n");
	struct timeval start;
	struct timeval end;
	int CID = 0;
	
	CID = *((int *)id);
	
	
    while(1){
		sem_wait(&full);
		pthread_mutex_lock(&mutex);
		if(destroyP == NUMBER_OF_PROCESSES){
			sem_post(&full);
			pthread_mutex_unlock(&mutex);
			pthread_exit(NULL);
		}

		struct process * temp = removeHead(&head);

		pthread_mutex_unlock(&mutex);
        sem_post(&empty);
        
        int resp = 0;
		int turn = 0;
		long int initial_burst_time = temp->iBurstTime;
		simulateSJFProcess(temp, &start, &end);
		turn = getDifferenceInMilliSeconds(temp->oTimeCreated, end);
		resp = getDifferenceInMilliSeconds(temp->oTimeCreated, start);
		
		printf("CONSUMER ID = %d Process ID = %d, Previous Burst Time = %ld, New Burst Time = %d , Response Time = %d, Turn Around Time = %d\n", 
            CID,
            temp->iProcessId, 
			initial_burst_time, 
			temp->iBurstTime, 
			resp, 
			turn);
		
            
		free(temp);
		pthread_mutex_lock(&valMutex);
		totalResp += resp;
		totalTurn += turn;
		destroyP++;
		pthread_mutex_unlock(&valMutex);
		
		pthread_mutex_lock(&mutex);
		if(destroyP == NUMBER_OF_PROCESSES){
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
	sem_init(&empty,0,BUFFER_SIZE);
	sem_init(&full,0,0);
    
    pthread_create(&producer, NULL, (void*)funcProducer, NULL);
    for(i = 0; i<NUMBER_OF_CONSUMERS; i++){
		cid[i] = i + 1;
        if (pthread_create(&(consumer[i]), NULL, (void*)funcConsumer, (void *) &(cid[i])) == -1) {
			printf("Thread creation failed\n");
		}
    }
	
    pthread_join(producer, NULL);
    for(i = 0; i<NUMBER_OF_CONSUMERS;i++){
        pthread_join(consumer[i], NULL);
	}
	printf("The average Response Time is: %f\n", (double)totalResp / NUMBER_OF_PROCESSES);
	printf("The average Turn Around Time is: %f\n", (double)totalTurn / NUMBER_OF_PROCESSES);
    
}