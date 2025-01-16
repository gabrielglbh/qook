import { StringFormatters } from "../../components/StringFormatters";
import TagDto from "./TagDto";

class Tag {
  id: string;
  text: string;

  constructor(
    id: string,
    text: string,
  ) {
    this.id = id;
    this.text = text;
  }
}

export const tagToDto = (tag: Tag) => {
  const keywords: string[] = [];
  keywords.push(...tag.text.split(' ').map(e => e.toLowerCase()));
  keywords.push(...StringFormatters.generateSubStrings(tag.text)); 
    
  return new TagDto(
    tag.id,
    tag.text,
    keywords,
  );
}

export default Tag;
