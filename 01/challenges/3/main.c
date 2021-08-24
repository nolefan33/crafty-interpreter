#include "main.h"

struct Node *root = NULL;

int main() {
    while (true) {
        int menu_selection;
        printf("1. Insert\n2. Find\n3. Delete Position (0-indexed)\n4. Delete Value\n5. Print List\n6. Exit\nChoose an option: ");
        scanf("%d", &menu_selection);

        switch (menu_selection) {
            case 1:
                insert();
                break;
            case 2:
                find();
                break;
            case 3:
                deletePosition();
                break;
            case 4:
                deleteValue();
                break;
            case 5:
                printListElements();
                break;
            case 6:
                printf("Bye!\n");
                return 0;
                break;
            default:
                printf("Option not recognized.\n");
        }
    }
    return 1;
}

void _insert_end(char *insert_value) {
    struct Node *new_node = malloc(sizeof(struct Node));
    
    strcpy(new_node->value, insert_value);
    
    if (root == NULL) {
        root = new_node;
    } else {
        struct Node *search = root;
        while (search->next != NULL) {
            search = search->next;
        }

        new_node->prev = search;
        search->next = new_node;
    }
}

int _find(char *search_value) {
    struct Node *curr_node = root;
    int count;

    while (curr_node != NULL) {
        if (strcmp(curr_node->value, search_value) == 0) {
            return count;
        } else {
            if (curr_node->next == NULL) {
                return -1;
            } else {
                curr_node = curr_node->next;
                ++count;
            }
        }
    }

    return -1; // should be unreachable
}

int _delete(int position) {
    struct Node *curr_node = root;
    for (int i = 0; i < position; ++i) {
        if (curr_node == NULL) {
            return -1;
        }
        curr_node = curr_node->next;
    }

    curr_node->prev->next = curr_node->next;
    if (curr_node->next != NULL) {
        curr_node->next->prev = curr_node->prev;
    }

    free(curr_node);
    return 0;
}

void insert() {
    int rc;
    char insert_value[100];
    
    rc = get_line("Enter string to insert: ", insert_value, 100);
    if (rc == NO_INPUT) {
        printf("No input given.\n");
        return;
    }
    
    _insert_end(insert_value);
}

void find() {
    char search_value[100];
    int rc, pos;

    rc = get_line("Enter string to find: ", search_value, 100);
    if (rc == NO_INPUT) {
        printf("No input given.\n");
        return;
    }
 
    pos = _find(search_value);
    if (pos >= 0) {
        printf("Found string \"%.100s\" at position %d\n", search_value, pos);
    } else {
        printf("Value \"%.100s\" not found.\n", search_value);
    }
}

void deletePosition() {
    int position;
    printf("Which position do you want to delete? ");
    scanf("%d", &position);

    if (position < 0) {
        printf("Invalid position, not deleting.\n");
    } else {
        printf("Deleting from position %d", position);
        _delete(position);
    }
}

void deleteValue() {
    char search_value[100];
    int rc, pos;
    
    rc = get_line("Enter string to delete: ", search_value, 100);
    if (rc == NO_INPUT) {
        printf("No input given.\n");
        return;
    }

    pos = _find(search_value);
    if (pos >= 0) {
        printf("Found string \"%.100s\" at position %d, deleting.\n", search_value, pos);
        _delete(pos);
    } else {
        printf("String \"%.100s\" not found in list, not deleting.\n", search_value);
    }
}

void printListElements() {
    int count = 0;
    struct Node *iter = root;
    while (iter != NULL) {
        printf("\"%.100s\"", iter->value);
        if (iter->next != NULL) {
            printf(", ");
        } else {
            printf("\n");
        }

        ++count;
        iter = iter->next;
    }
    printf("Nodes in list: %d\n", count);
    fflush (stdout);
}
