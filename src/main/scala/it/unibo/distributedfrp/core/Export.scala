package it.unibo.distributedfrp.core

trait Export[+A]:
  def root: A
  def followPath(path: Seq[Any]): Option[Export[Any]]

object Export:
  case class ExportImpl[+A](root: A, children: Map[Any, Export[Any]]) extends Export[A]:
    def followPath(path: Seq[Any]): Option[Export[Any]] = path match
      case h :: t => children.get(h).flatMap(_.followPath(t))
      case _ => Some(this)

  def apply[A](root: A, children: Map[Any, Export[Any]]): Export[A] = ExportImpl(root, children)

  def atomic[A](value: A): Export[A] = Export(value, Map.empty)

  def wrapper[A](value: A, key: Any, child: Export[Any]): Export[A] = Export(value, Map(key -> child))

  def ordered[A](value: A, children: Seq[Export[Any]]): Export[A] =
    Export(value, children.zipWithIndex.map(_.swap).toMap)
