#include <stdio.h>
#include <stdlib.h>
#include "coursework.h"
struct process *head = NULL;
struct process *tail = NULL;

void addToList(struct process **head, struct process * new, struct process **tail){
	if(*head == NULL){
		*head = new;
		*tail = new;
	} else{
		(*tail)->oNext = new;
		*tail = new;
		(*tail)->oNext = NULL;
	
	}	
}

void removeFromList(struct process **head){
	struct process *temp = NULL;
	temp = *head;
	*head = (*head)->oNext;
}


void main(){
	struct process *newProcess = NULL;
	
	int i = 0;
	
	for(i = 0; i < NUMBER_OF_PROCESSES; i++){
		newProcess = generateProcess();
		addToList(&head, newProcess, &tail);
	} 
	
	struct timeval start;
	struct timeval end;
	int totalResp = 0;
	int totalTurn = 0;

	while(head != NULL){
		int resp = 0;
		int turn = 0;
		int id = 0;
		
		int initial_burst_time = head->iBurstTime;
		int check = head->iState;
		simulateRoundRobinProcess(head, &start, &end);
		turn = getDifferenceInMilliSeconds(head->oTimeCreated, end);
		resp = getDifferenceInMilliSeconds(head->oTimeCreated, start);
		
		printf("Process ID = %d, Previous Burst Time = %d, New Burst Time = %d",
			head->iProcessId, 
			initial_burst_time, 
			head->iBurstTime
		);
		if(check == NEW){
			printf(", Response Time = %d", resp);
			totalResp += resp;
		}
		if(head->iState == FINISHED){
			printf(", Turn Around Time = %d ", turn);
			totalTurn += turn;
		}
		printf("\n");	

		if(head->iState == FINISHED){
			struct process *destroy = head;
			removeFromList(&head);
			free(destroy);
		} else {
			struct process *add = head;
			removeFromList(&head);			
			addToList(&head, add, &tail);
		}
	}
	printf("The average Response Time is: %f\n", (double)totalResp / NUMBER_OF_PROCESSES);
	printf("The average Turn Around Time is: %f\n", (double)totalTurn / NUMBER_OF_PROCESSES);	
}