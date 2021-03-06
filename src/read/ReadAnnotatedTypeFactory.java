package read;

import javax.lang.model.element.AnnotationMirror;

import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.flow.CFAbstractAnalysis;
import org.checkerframework.framework.flow.CFStore;
import org.checkerframework.framework.flow.CFValue;
import org.checkerframework.framework.type.AnnotatedTypeFactory;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.type.GenericAnnotatedTypeFactory;
import org.checkerframework.framework.type.treeannotator.ImplicitsTreeAnnotator;
import org.checkerframework.framework.type.treeannotator.ListTreeAnnotator;
import org.checkerframework.framework.type.treeannotator.PropagationTreeAnnotator;
import org.checkerframework.framework.type.treeannotator.TreeAnnotator;
import org.checkerframework.javacutil.AnnotationBuilder;

import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.UnaryTree;

import read.qual.SafeRead;
import read.qual.UnsafeRead;

public class ReadAnnotatedTypeFactory extends GenericAnnotatedTypeFactory<CFValue, CFStore, ReadTransfer, ReadAnalysis> {
    protected AnnotationMirror UNSAFE_READ;
    protected AnnotationMirror SAFE_READ;

    public ReadAnnotatedTypeFactory(BaseTypeChecker checker) {
        super(checker);
        this.postInit();

        UNSAFE_READ = AnnotationBuilder.fromClass(elements, UnsafeRead.class);
        SAFE_READ = AnnotationBuilder.fromClass(elements, SafeRead.class);
    }

    @Override
    public ReadTransfer createFlowTransferFunction(CFAbstractAnalysis<CFValue, CFStore, ReadTransfer> analysis) {
        return new ReadTransfer(analysis);
    }
    protected class ReadTreeAnnotator extends TreeAnnotator {

        public ReadTreeAnnotator(AnnotatedTypeFactory atypeFactory) {
            super(atypeFactory);
        }

        @Override
        public Void visitNewClass(NewClassTree node, AnnotatedTypeMirror type) {
            super.visitNewClass(node, type);
            type.replaceAnnotation(SAFE_READ);
            return null;
        }

        @Override
        public Void visitUnary(UnaryTree node, AnnotatedTypeMirror type) {
            if (type.hasAnnotation(UNSAFE_READ)) {
                type.replaceAnnotation(SAFE_READ);
            }
            return null;
        }

        @Override
        public Void visitBinary(BinaryTree node, AnnotatedTypeMirror type) {
            if (type.hasAnnotation(UNSAFE_READ)) {
                type.replaceAnnotation(SAFE_READ);
            }
            return null;
        }
    }

    @Override
    protected TreeAnnotator createTreeAnnotator() {
        return new ListTreeAnnotator(
                new PropagationTreeAnnotator(this),
                new ImplicitsTreeAnnotator(this),
                new ReadTreeAnnotator(this)
                );
    }
}
