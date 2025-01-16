import { StringFormatters } from "../../components/StringFormatters";
import TagDto from "./TagDto";

interface Tag {
  toDto(): TagDto;
}

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

Tag.prototype.toDto = function() {
  const keywords: string[] = [];
  keywords.push(...this.text.split(' ').map(e => e.toLowerCase()));
  keywords.push(...StringFormatters.generateSubStrings(this.text)); 
    
  return new TagDto(
    this.id,
    this.text,
    keywords,
  );
}

export default Tag;
