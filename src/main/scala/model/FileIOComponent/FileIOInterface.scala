package model.FileIOComponent

import model.IMatrix

trait FileIOInterface[T] {
  def load: IMatrix[T]
  def save(matrix: IMatrix[T]): Unit
}



