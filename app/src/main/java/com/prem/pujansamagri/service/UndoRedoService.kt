package com.prem.pujansamagri.service

import com.prem.pujansamagri.models.CanvasStackItem
import java.util.Stack

class UndoRedoService  {

     private val undoStack = Stack<CanvasStackItem>()
     private val redoStack = Stack<CanvasStackItem>()

    fun undo(): CanvasStackItem{
       val undoItem = popUndoStack()

       if (undoStackSize() == 0) {
           pushUndoStack(undoItem)
           return undoItem
       }

       pushRedoStack(undoItem)
       return undoStack.peek()
    }

    fun redo(): CanvasStackItem?{
        if (redoStack.size == 0){
            return null
        }
        val stackItem = popRedoStack()
        pushUndoStack(stackItem)

        return stackItem
    }

    fun undoStackSize(): Int = undoStack.size
    fun redoStackSize(): Int = redoStack.size

    fun pushUndoStack(stackItem: CanvasStackItem){
        undoStack.push(stackItem)
    }

    private fun popUndoStack(): CanvasStackItem = undoStack.pop()


    private fun pushRedoStack(stackItem: CanvasStackItem){
        redoStack.push(stackItem)
    }

    private fun popRedoStack(): CanvasStackItem = redoStack.pop()

    fun clearRedoStack() = redoStack.clear()
}