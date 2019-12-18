package net.sf.colorer.eclipse.jface;

import java.awt.TextComponent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextInputListener;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.graphics.Color;

public class ColorerContentAssistProcessor implements IContentAssistProcessor, ITextListener, ITextInputListener, Runnable
{
    String lastWord = "";
    boolean first = true;
    ArrayList<String> proposals = new ArrayList<String>(){{
        add("suggestion");
        add("hello");
        
    }};
    
    public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
        viewer.addTextListener(this);
        int p = 0;
        for (int i= 0; i < proposals.size(); i++) {  
            if(proposals.get(i).startsWith(lastWord)){
                p++;  
            }
        }
        ICompletionProposal[] result= new ICompletionProposal[p];
        
        for (int i= 0, k = 0; i < proposals.size(); i++) { 
            if(proposals.get(i).startsWith(lastWord)){
                result[k] = new CompletionProposal(proposals.get(i), offset - lastWord.length(), lastWord.length(), proposals.get(i).length());
                k++;
            }
        }
        return result;
    }

    public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
        return null;
    }

    public char[] getCompletionProposalAutoActivationCharacters() {
        return null;
    }

    public char[] getContextInformationAutoActivationCharacters() {
        return null;
    }

    public IContextInformationValidator getContextInformationValidator() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getErrorMessage() {
        return "No proposals";
    }

    @Override
    public void textChanged(TextEvent event){
        String replaced = event.getReplacedText();
        String lastInput = event.getText();

        System.out.println(lastInput);
        if(first){
            lastWord = new String();
            first = false;
        }
        else if(replaced != null){
            lastWord = lastWord.replace(lastWord.substring(lastWord.length()-1), "");
        }
        else{
            System.out.println("we adding");
            char last = lastInput.charAt(lastInput.length() -1);
            if(Character.isWhitespace(last)){
                if(lastWord.length() > 2 && !proposals.contains(lastWord)){
                    proposals.add(lastWord);
                }
                
                lastWord = new String();
            }
            else{
                lastWord += lastInput;
            }   
        }
        System.out.println(lastWord);
        
    }

    @Override
    public void inputDocumentAboutToBeChanged(IDocument oldInput,
            IDocument newInput) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void inputDocumentChanged(IDocument oldInput, IDocument newInput) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        
    }



}

/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is the Colorer Library
 *
 * The Initial Developer of the Original Code is
 * Igor Russkih <irusskih at gmail dot com>.
 * Portions created by the Initial Developer are Copyright (C) 1999-2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */